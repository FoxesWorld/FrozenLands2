package org.foxesworld.engine.providers.material.attributes;

import com.jme3.math.Vector3f;

public class MaterialValue {
    private String type;
    private float floatValue;
    private boolean booleanValue;
    private String colorValue;
    private String vectorValue;

    public MaterialValue(String type, float floatValue, boolean booleanValue, String colorValue, String vectorValue) {
        this.type = type;
        this.floatValue = floatValue;
        this.booleanValue = booleanValue;
        this.colorValue = colorValue;
        this.vectorValue = vectorValue;
    }

    public String getType() {
        return type;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public String getColorValue() {
        return colorValue;
    }

    public Vector3f getVectorValue() {
        String[] components = vectorValue.split(",");
        float x = Float.parseFloat(components[0]);
        float y = Float.parseFloat(components[1]);
        float z = Float.parseFloat(components[2]);
        return new Vector3f(x, y, z);
    }
}
