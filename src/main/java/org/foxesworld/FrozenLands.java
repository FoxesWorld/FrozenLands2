package org.foxesworld;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.engine.Engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FrozenLands extends SimpleApplication {

    public static final Logger logger =  LogManager.getLogger(FrozenLands.class);
    private BulletAppState bulletAppState;
    private Engine engine;

    public static void main(String[] args) {
        FrozenLands app = new FrozenLands();
        app.setShowSettings(true);
        var cfg = new AppSettings(false);
        cfg.setVSync(false);
        cfg.setResolution(640, 480);
        cfg.setFullscreen(true);
        cfg.setSamples(16);
        cfg.setTitle("FrozenLands");
        setIcon(cfg);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.engine = new Engine(this, "userInput");
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        stateManager.attach(this.engine);
        this.flyCam.setMoveSpeed(32);

        Spatial model = assetManager.loadModel("meshes/Vulpine/VulpineTailSilver.obj");
        rootNode.attachChild(model);
    }

    private static void setIcon(AppSettings settings) {
        try {
            BufferedImage[] icons = new BufferedImage[]{
                    ImageIO.read(FrozenLands.class.getClassLoader().getResource("textures/gui/test64.png")),
                    ImageIO.read(FrozenLands.class.getClassLoader().getResource("textures/gui/test32.png")),
                    ImageIO.read(FrozenLands.class.getClassLoader().getResource("textures/gui/test16.png"))
            };
            settings.setIcons(icons);
        } catch (IOException ignored) {
        }
    }

    public Engine getEngine() {
        return engine;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public AppStateManager getStateManager() {
        return this.stateManager;
    }
}
