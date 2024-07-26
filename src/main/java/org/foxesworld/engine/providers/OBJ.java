package org.foxesworld.engine.providers;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Spatial;
import org.foxesworld.engine.Engine;

public class OBJ {

    private final Engine engine;
    private Spatial model;
    private  RigidBodyControl objectControl;
    private CollisionShape objectShape;

    public OBJ(Engine engine){
        this.engine = engine;
    }

    public OBJ getModel(String path, float scale){
        model = this.engine.getFrozenLands().getAssetManager().loadModel(path);
        model.scale(scale);

        objectShape = CollisionShapeFactory.createMeshShape(model);
        objectControl = new RigidBodyControl(objectShape, model.getTriangleCount());
        model.addControl(objectControl);

        this.engine.getFrozenLands().getBulletAppState().getPhysicsSpace().add(objectControl);
        return this;
    }

    public Spatial getModel() {
        return model;
    }

    public RigidBodyControl getObjectControl() {
        return objectControl;
    }

    public CollisionShape getObjectShape() {
        return objectShape;
    }
}
