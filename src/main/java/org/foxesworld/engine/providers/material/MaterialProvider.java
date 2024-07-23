package org.foxesworld.engine.providers.material;

import com.google.gson.Gson;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.providers.material.attributes.*;

import java.util.HashMap;
import java.util.Map;

import static org.foxesworld.engine.utils.InputReader.inputReader;

public class MaterialProvider extends MaterialAbstract {
    private final String matDir = "Materials/";
    private final Map<String, Material> materials = new HashMap<>();
    private final FrozenLands frozenLands;

    public MaterialProvider(FrozenLands frozenLands) {
        this.frozenLands = frozenLands;
        setAssetManager(frozenLands);
    }

    @Override
    public void loadMaterials(String path) {
        FrozenLands.logger.info("Adding materials");
        for (Materials mat : new Gson().fromJson(inputReader(path), Materials[].class)) {
            for(String subMat: mat.getSubMats()){
                FrozenLands.logger.info("Adding '" + mat.getMatName() + "' material of type " +subMat);
                materials.put(mat.getMatName() + '#' + subMat, createMat(mat.getMatName(), subMat));
            }

        }

        FrozenLands.logger.info("Finished adding materials, total matAmount: " + materials.size());
    }

    @Override
    public Material createMat(String dir, String type) {
        int textureNum = 0, varNum = 0;
        //MaterialDef materialDef = null;
        String baseDir = matDir + dir + '/';
        MatOpt matOpt = readMatConfig(baseDir + "/" + type + ".fgsm");
        initMaterial(matOpt.getMatDef());
        getMaterial().setName(dir + '#' + type);
        for (TextureInstance textureInstance : matOpt.getTextures()) {
            TextureKey key = new TextureKey("textures/" + textureInstance.getRegOptions().getTexture(), false);
            key.setGenerateMips(false);

            Image image = frozenLands.getAssetManager().loadTexture(key).getImage();
            image.setHeight(textureInstance.getRegOptions().getUvSize().getHeight());
            image.setWidth(textureInstance.getRegOptions().getUvSize().getWidth());
            Texture thisTexture = new Texture2D(image);
            thisTexture.setName(key.getName());
            thisTexture.setWrap(Texture.WrapMode.valueOf(textureInstance.getRegOptions().getWrap()));
            getMaterial().setTexture(textureInstance.textureParam(), thisTexture);
            textureNum++;
            FrozenLands.logger.info("Adding {} texture to {}", thisTexture, dir);
        }

        for (ParamData varOption : matOpt.getParams()) {
            inputType(varOption, dir);
            varNum++;
        }
        FrozenLands.logger.info(dir + '#' + type + " has " + textureNum + " textures and " + varNum + " vars");

        return getMaterial();
    }

    private void inputType(ParamData varOption, String material) {
        VarType inputType = VarType.valueOf(varOption.getParamOpt().getType().toUpperCase());
        String paramName = varOption.getParamName();
        Object value = varOption.getParamOpt().getValue();
        switch (inputType) {
            case FLOAT -> setMaterialFloat(paramName, Integer.parseInt((String) value));
            case BOOLEAN -> setMaterialBoolean(paramName, Boolean.getBoolean((String) value));
            case COLOR -> setMaterialColor(paramName, parseColor((String) value));
            case INT -> setMaterialInt(paramName, Integer.parseInt((String) value));
            case VECTOR -> {
                String[] valStr = String.valueOf(value).split(",");
                Vector3f vector3f = new Vector3f(Integer.parseInt(valStr[0]), Integer.parseInt(valStr[1]), Integer.parseInt(valStr[2]));
                setMaterialVector(paramName, vector3f);
            }
        }
        FrozenLands.logger.info("Adding param {} for material {}", paramName, material);
    }

    private ColorRGBA parseColor(String colorStr) {
        String[] rgba = colorStr.split(",");
        float r = Float.parseFloat(rgba[0]);
        float g = Float.parseFloat(rgba[1]);
        float b = Float.parseFloat(rgba[2]);
        float a = Float.parseFloat(rgba[3]);
        return new ColorRGBA(r, g, b, a);
    }

    private MatOpt readMatConfig(String path) {
        return new Gson().fromJson(inputReader(path), MatOpt.class);
    }

    private enum VarType {
        FLOAT,
        VECTOR,
        BOOLEAN,
        COLOR,
        INT
    }

    public Material getMaterial(String mat) {
        return materials.get(mat);
    }
}
