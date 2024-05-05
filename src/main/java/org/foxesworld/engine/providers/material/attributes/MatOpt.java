package org.foxesworld.engine.providers.material.attributes;

import java.util.List;
public class MatOpt {
    private String matDef;
    private List<TextureInstance> textures;
    private List<VarData> vars;
    public String getMatDef() {
        return matDef;
    }

    public List<TextureInstance> getTextures() {
        return textures;
    }

    public List<VarData> getVars() {
        return vars;
    }
}