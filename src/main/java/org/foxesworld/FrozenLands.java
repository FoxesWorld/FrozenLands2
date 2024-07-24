package org.foxesworld;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.engine.Engine;
import org.foxesworld.engine.player.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FrozenLands extends SimpleApplication {

    public static final Logger logger =  LogManager.getLogger(FrozenLands.class);
    private BulletAppState bulletAppState;
    private Engine engine;
    private Player player;

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
        stateManager.getState(StatsAppState.class).toggleStats();
        player = new Player(this);
        flyCam.setMoveSpeed(100);

        Spatial model = assetManager.loadModel("meshes/Vulpine/VulpineTailSilver.obj");
        rootNode.attachChild(model);

        this.plane();
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

    private void plane() {
        Box planeShape = new Box(Vector3f.ZERO, 20f, 0.5f, 20f);
        Geometry plane = new Geometry("Plane", planeShape);
        plane.setMaterial(this.engine.getMaterialProvider().getMaterial("terrain#default"));

        CollisionShape sceneShape = new BoxCollisionShape(planeShape.getXExtent(), planeShape.getYExtent(), planeShape.getZExtent());
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        plane.addControl(landscape);

        plane.setLocalTranslation(0f, -1f, 0f);

        this.rootNode.attachChild(plane);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player.getPlayer());
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.getCamDir().set(cam.getDirection()).multLocal(0.3f);
        player.getCamLeft().set(cam.getLeft()).multLocal(0.4f);
        player.getWalkDirection().set(0, 0, 0);
        if (player.isLeft()) {
            player.getWalkDirection().addLocal(player.getCamLeft());
        }
        if (player.isRight()) {
            player.getWalkDirection().addLocal(player.getCamLeft().negate());
        }
        if (player.isUp()) {
            player.getWalkDirection().addLocal(player.getCamDir());
        }
        if (player.isDown()) {
            player.getWalkDirection().addLocal(player.getCamDir().negate());
        }
        player.getPlayer().setWalkDirection(player.getWalkDirection());
        cam.setLocation(player.getPlayer().getPhysicsLocation());
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
