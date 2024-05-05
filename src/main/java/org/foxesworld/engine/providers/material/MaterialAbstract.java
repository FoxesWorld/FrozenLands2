package org.foxesworld.engine.providers.material;

import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.foxesworld.FrozenLands;

public abstract class MaterialAbstract {
    private FrozenLands frozenLands;
    private Material material;
    private MaterialDef materialDef;
    public abstract void loadMaterials(String path);
    public abstract  Material createMat(String dir, String type);
    public void setMaterialBoolean(String map, boolean val) {
        getMaterial().setBoolean(map, val);
    }

    public void setMaterialVector(String map, Vector3f vector) {
        getMaterial().setVector3(map, vector);
    }

    public void setMaterialFloat(String map, float val) {
        getMaterial().setFloat(map, val);
    }
    public void setMaterialColor(String map, ColorRGBA color) {
        try {
            getMaterial().setColor(map, color);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void  setMaterialInt(String map, int val) {
        getMaterial().setInt(map, val);
    }

    public MaterialDef getMaterialDef() {
        return materialDef;
    }
    protected void initMaterial(String matDef) {
        material = new Material(this.frozenLands.getAssetManager(), matDef);
    }
    public FrozenLands getFrozenLands() {
        return frozenLands;
    }
    public void setAssetManager(FrozenLands frozenLands) {
        this.frozenLands = frozenLands;
    }
    public Material getMaterial() {
        return material;
    }
}
