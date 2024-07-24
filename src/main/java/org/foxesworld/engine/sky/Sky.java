package org.foxesworld.engine.sky;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.StarsOption;
import jme3utilities.sky.Updater;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.Updateable;

import java.util.Calendar;

public class Sky implements Updateable {

    private String skyTexture = "textures/world/environment/skyBox.dds";
    private Vector3f sunDirection = new Vector3f(-1f, -1f, -1f);
    private ColorRGBA sunColor = ColorRGBA.White;
    private ColorRGBA ambientColor = ColorRGBA.DarkGray;
    private DirectionalLight sun;
    private final Node rootNode;
    private final AssetManager assetManager;
    private final ViewPort viewPort;
    private final Camera camera;
    private final FrozenLands frozenLands;
    private SkyControl skyControl;
    private Updater updater;
    private DirectionalLightShadowRenderer dlsr;

    public Sky(FrozenLands kernel) {
        this.frozenLands = kernel;
        this.rootNode = kernel.getRootNode();
        this.assetManager = kernel.getAssetManager();
        this.camera = kernel.getCamera();
        this.viewPort = kernel.getViewPort();
        this.createSky();
    }

    public void updateTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        float timeOfDay = hour + minute / 60f;

        // Define the colors for each time of day
        ColorRGBA dawnColor = new ColorRGBA(1, 0.5f, 0.2f, 1); // warm orange color
        ColorRGBA dayColor = new ColorRGBA(1, 1, 0.8f, 1); // bright blue color
        ColorRGBA duskColor = new ColorRGBA(0.8f, 0.5f, 0.2f, 1); // warm orange color
        ColorRGBA nightColor = new ColorRGBA(0.2f, 0.2f, 0.5f, 1); // dark blue color

        // Define the time ranges for each color
        float dawnRange = 5.0f / 24.0f; // 5am to 6am (dawn)
        float dayRange = 12.0f / 24.0f; // 12pm to 3pm (day)
        float duskRange = 18.0f / 24.0f; // 6pm to 7pm (dusk)
        float nightRange = 22.0f / 24.0f; // 10pm to 5am (night)

        // Calculate the color based on the time of day
        ColorRGBA skyColor;
        if (timeOfDay < dawnRange) {
            skyColor = nightColor;
        } else if (timeOfDay < dayRange) {
            skyColor = interpolateColor(nightColor, dawnColor, (timeOfDay - dawnRange) / (dayRange - dawnRange));
        } else if (timeOfDay < duskRange) {
            skyColor = interpolateColor(dawnColor, dayColor, (timeOfDay - dayRange) / (duskRange - dayRange));
        } else if (timeOfDay < nightRange) {
            skyColor = interpolateColor(dayColor, duskColor, (timeOfDay - duskRange) / (nightRange - duskRange));
        } else {
            skyColor = interpolateColor(duskColor, nightColor, (timeOfDay - nightRange) / (1.0f - nightRange));
        }

        // Set the sky color
        this.setTimeOfDay(timeOfDay);
        this.setSunColor(skyColor);
        this.setAmbientColor(skyColor);
    }

    private ColorRGBA interpolateColor(ColorRGBA start, ColorRGBA end, float t) {
        float r = start.r + (end.r - start.r) * t;
        float g = start.g + (end.g - start.g) * t;
        float b = start.b + (end.b - start.b) * t;
        return new ColorRGBA(r, g, b, 1);
    }

    private void createSky() {
        var gi = new AmbientLight(ambientColor);
        sun = new DirectionalLight(sunDirection.normalizeLocal());
        sun.setColor(sunColor.mult(1f));
        dlsr = new DirectionalLightShadowRenderer(assetManager, 4096, 1);
        dlsr.setLight(sun);

        Spatial sky = SkyFactory.createSky(assetManager, skyTexture, SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(sky);
        skyControl = new SkyControl(assetManager, camera, .5f, StarsOption.TopDome, true);
        rootNode.addControl(skyControl);
        skyControl.setCloudiness(0.1f);
        skyControl.setCloudsYOffset(0.4f);
        skyControl.setTopVerticalAngle(1.78f);
        skyControl.getSunAndStars().setHour(11);
        updater = skyControl.getUpdater();
        updater.setAmbientLight(gi);
        updater.setMainLight(sun);
        updater.addShadowRenderer(dlsr);
        skyControl.setEnabled(true);
        rootNode.addLight(sun);
        addShadows();
    }

    private void addShadows() {
        FilterPostProcessor processor = new FilterPostProcessor(assetManager);
        DirectionalLightShadowFilter filter = new DirectionalLightShadowFilter(assetManager, 2048, 1);
        filter.setLight(this.sun);
        processor.addFilter(filter);
        viewPort.addProcessor(processor);
    }

    // DayTime
    public void setTimeOfDay(float hour) {
        skyControl.getSunAndStars().setHour(hour);
        // Light & shadow update
        updater.setMainLight(sun);
        updater.addShadowRenderer(dlsr);
    }

    public float getTimeOfDay() {
        return skyControl.getSunAndStars().getHour();
    }

    // Weather
    public void setCloudiness(float cloudiness) {
        skyControl.setCloudiness(cloudiness);
    }

    public float getCloudiness() {
        return skyControl.getCloudsRate();
    }

    public void setSunColor(ColorRGBA color) {
        this.sunColor = color;
        sun.setColor(color.mult(1f));
        updater.setMainLight(sun);
    }

    public ColorRGBA getSunColor() {
        return this.sunColor;
    }

    public void setAmbientColor(ColorRGBA color) {
        this.ambientColor = color;
        updater.setAmbientLight(new AmbientLight(ambientColor));
    }

    public ColorRGBA getAmbientColor() {
        return this.ambientColor;
    }

    @Override
    public void update(float tpf) {
        this.updateTimeOfDay();
    }
}