package org.foxesworld.engine.providers.obj;

import com.jme3.math.ColorRGBA;

public class MaterialData {
    private ColorRGBA ambientColor = ColorRGBA.Black;
    private ColorRGBA diffuseColor = ColorRGBA.Black;
    private ColorRGBA specularColor = ColorRGBA.Black;
    private String diffuseMap;
    private float shininess = 0;

    public ColorRGBA getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(ColorRGBA ambientColor) {
        this.ambientColor = ambientColor;
    }

    public ColorRGBA getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(ColorRGBA diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public ColorRGBA getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(ColorRGBA specularColor) {
        this.specularColor = specularColor;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public String getDiffuseMap() {
        return diffuseMap;
    }

    public void setDiffuseMap(String diffuseMap) {
        this.diffuseMap = diffuseMap;
    }
}
