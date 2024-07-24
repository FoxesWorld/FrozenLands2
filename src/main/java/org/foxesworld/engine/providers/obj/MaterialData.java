package org.foxesworld.engine.providers.obj;

import com.jme3.math.ColorRGBA;

public class MaterialData {
    private ColorRGBA ambientColor;
    private ColorRGBA diffuseColor;
    private ColorRGBA specularColor;
    private float shininess;
    private String diffuseMap;
    private String normalMap;

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

    public String getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(String normalMap) {
        this.normalMap = normalMap;
    }
}
