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

public class Sky {

    private  String skyTexture = "textures/environment/skyBox.dds";
    private Vector3f sunDirection = new Vector3f(-1f, -1f, -1f);
    private ColorRGBA sunColor = ColorRGBA.White;
    private ColorRGBA ambientColor = ColorRGBA.DarkGray;
    private DirectionalLight sun;
    private final Node rootNode;
    private final AssetManager assetManager;
    private final ViewPort viewPort;
    private final Camera camera;
    private final FrozenLands frozenLands;
    public Sky(FrozenLands kernel){
        this.frozenLands = kernel;
        this.rootNode = kernel.getRootNode();
        this.assetManager = kernel.getAssetManager();
        this.camera = kernel.getCamera();
        this.viewPort = kernel.getViewPort();
    }

    public void addSky(){
        var gi = new AmbientLight(ambientColor);
        sun = new DirectionalLight(sunDirection.normalizeLocal());
        sun.setColor(sunColor.mult(1f));
        var dlsr = new DirectionalLightShadowRenderer(assetManager, 4096, 1);
        dlsr.setLight(sun);

        Spatial sky = SkyFactory.createSky(assetManager, skyTexture, SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
        SkyControl skyControl = new SkyControl(assetManager, camera, .5f, StarsOption.TopDome, true);
        rootNode.addControl(skyControl);
        skyControl.setCloudiness(0.8f);
        skyControl.setCloudsYOffset(0.4f);
        skyControl.setTopVerticalAngle(1.78f);
        skyControl.getSunAndStars().setHour(11);
        Updater updater = skyControl.getUpdater();
        updater.setAmbientLight(gi);
        updater.setMainLight(sun);
        updater.addShadowRenderer(dlsr);
        skyControl.setEnabled(true);
        rootNode.addLight(sun);
        addShadows();
    }

    private  void addShadows(){
        FilterPostProcessor processor = new FilterPostProcessor(assetManager);
        DirectionalLightShadowFilter filter = new DirectionalLightShadowFilter(assetManager, 2048, 1);
        filter.setLight(this.sun);
        processor.addFilter(filter);
        viewPort.addProcessor(processor);
    }
}
