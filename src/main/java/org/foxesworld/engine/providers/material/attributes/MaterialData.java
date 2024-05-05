package org.foxesworld.engine.providers.material.attributes;

import java.util.List;

public class MaterialData {
    private  String matDef;
    private List<TextureData> textures;
    private List<VarData> vars;

    public List<TextureData> getTextures() {
        return textures;
    }

    public List<VarData> getVars() {
        return vars;
    }

    public String getMatDef() {
        return matDef;
    }
}
