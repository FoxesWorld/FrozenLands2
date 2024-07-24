package org.foxesworld.engine.terrain;

import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.*;
import org.foxesworld.engine.Engine;

import java.util.List;
import java.util.stream.Stream;

@Deprecated
public class TerrainGenHelper {

    private Engine kernelInterface;
    private  TerrainQuad terrain;

    public TerrainGenHelper(Engine kernelInterface, TerrainQuad terrain){
        this.kernelInterface = kernelInterface;
        this.terrain = terrain;
    }

    void setupScale() {
        terrain.setLocalScale(128, 128, 64);
    }

    void setupPosition() {
        terrain.setLocalTranslation(0, 0, 0);
    }

    /*void setUpLODControl() {
        TerrainLodControl control = new TerrainGridLodControl(this.terrain, kernelInterface.getCamera());
        control.setLodCalculator(
                new DistanceLodCalculator(257, 2.7f));
        this.terrain.addControl(control);
    } */

    void setUpCollision() {
        ((TerrainGrid) terrain).addListener(new TerrainGridListener() {
            @Override
            public void gridMoved(Vector3f newCenter) {}

            @Override
            public void tileAttached(Vector3f cell, TerrainQuad quad) {
                //TreeGen treeGen = new TreeGen(kernelInterface);
                while (quad.getControl(RigidBodyControl.class) != null) {
                    quad.removeControl(RigidBodyControl.class);
                }
                quad.addControl(new RigidBodyControl(
                        new HeightfieldCollisionShape(
                                quad.getHeightMap(), terrain.getLocalScale()),
                        0));
                kernelInterface.getFrozenLands().getBulletAppState().getPhysicsSpace().add(quad);
                //treeGen.positionTrees(quad);
            }

            @Override
            public void tileDetached(Vector3f cell, TerrainQuad quad) {
                if (quad.getControl(RigidBodyControl.class) != null) {
                    kernelInterface.getFrozenLands().getBulletAppState().getPhysicsSpace().remove(quad);
                    quad.removeControl(RigidBodyControl.class);
                }
                List<Spatial> quadForest = quad.getUserData("quadForest");
                Stream<Spatial> stream = quadForest.stream();
                stream.forEach(treeNode -> {
                    //kernelInterface.getLogger().info("Detached " + treeNode.hashCode() + treeNode.getLocalTranslation().toString());
                    //kernelInterface.getRootNode().detachChild(treeNode);
                });
            }
        });
    }
}
