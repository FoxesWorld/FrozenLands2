package org.foxesworld.engine.providers;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Spatial;
import org.foxesworld.engine.Engine;

public class OBJ {

    private final Engine engine;

    public OBJ(Engine engine){
        this.engine = engine;
    }

    public Spatial getModel(String path, float scale){
        Spatial model = this.engine.getFrozenLands().getAssetManager().loadModel(path);
        model.scale(scale);

        CollisionShape objectShape = CollisionShapeFactory.createMeshShape(model);
        RigidBodyControl objectControl = new RigidBodyControl(objectShape, model.getTriangleCount());
        model.addControl(objectControl);

        this.engine.getFrozenLands().getBulletAppState().getPhysicsSpace().add(objectControl);
        return model;
    }
}
