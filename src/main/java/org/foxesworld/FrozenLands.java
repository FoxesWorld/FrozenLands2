package org.foxesworld;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.engine.Engine;
import org.foxesworld.engine.providers.material.MaterialProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FrozenLands extends SimpleApplication {

    public static final Logger logger =  LogManager.getLogger(FrozenLands.class);
    private BulletAppState bulletAppState;
    private Engine engine;

    public static void main(String[] args) {
        FrozenLands app = new FrozenLands();
        var cfg = new AppSettings(true);
        cfg.setVSync(false);
        cfg.setResolution(640, 480);
        cfg.setFullscreen(false);
        cfg.setSamples(16);
        cfg.setTitle("FrozenLands");
        setIcon(cfg);
        app.start();

    }

    @Override
    public void simpleInitApp() {
        this.engine = new Engine(this);
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        stateManager.attach(this.engine);

        Quad quadMesh = new Quad(10, 10);
        Geometry ground = new Geometry("Ground", quadMesh);
        ground.setMaterial(engine.getMaterialProvider().getMaterial("color#green"));

        ground.rotate(-FastMath.HALF_PI, 0, 0);
        rootNode.attachChild(ground);

        Player player = new Player(this.engine);
        player.setLocalTranslation(0, 1, 0);
        rootNode.attachChild(player);
    }

    private static void setIcon(AppSettings settings) {
        try {
            BufferedImage[] icons = new BufferedImage[]{
                    ImageIO.read(FrozenLands.class.getResource("/test64.png")),
                    ImageIO.read(FrozenLands.class.getResource("/test32.png")),
                    ImageIO.read(FrozenLands.class.getResource("/test16.png"))
            };
            settings.setIcons(icons);
        } catch (IOException ignored) {
        }
    }
    private class Player extends Geometry {
        public Player(Engine engine) {
            super("Player", new Box(0.5f, 1, 0.5f));
            Material playerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            playerMat.setColor("Color", ColorRGBA.Blue);
            setMaterial(engine.getMaterialProvider().getMaterial("color#pink"));
        }
    }
}
