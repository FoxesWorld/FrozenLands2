package org.foxesworld;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.engine.Engine;
import org.foxesworld.engine.player.Player;
import org.foxesworld.engine.providers.OBJ;

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
        //app.setShowSettings(true);
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
        flyCam.setEnabled(false);

        stateManager.getState(StatsAppState.class).toggleStats();
        player = new Player(this);
        OBJ playerObj = this.engine.getObj().getModel("meshes/Vulpine/FemaleBodyDigiFull.obj", 0.1f);
        Spatial playerModel = playerObj.getModel();
        playerModel.setCullHint(Spatial.CullHint.Never);
        playerModel.setShadowMode(RenderQueue.ShadowMode.Cast);
        playerModel.addControl(player.getPlayerControl());
        this.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0f, -50f, 0f));
        playerObj.getObjectControl().setCcdMotionThreshold(0f);
        rootNode.attachChild(playerModel);
        player.setModel(playerModel);


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
        Box planeShape = new Box(Vector3f.ZERO, 500f, 0.5f, 500f);
        Geometry plane = new Geometry("Plane", planeShape);
        plane.setMaterial(this.engine.getMaterialProvider().getMaterial("terrain#default"));

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(plane);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        plane.addControl(landscape);

        plane.setLocalTranslation(0f, -1f, 0f);

        this.rootNode.attachChild(plane);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player.getPlayerControl());
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);
        this.engine.getSky().update(tpf);
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
