package org.foxesworld.engine.providers.obj;

import com.jme3.math.ColorRGBA;

@FunctionalInterface
public interface ColorSetter {
    void setColor(MaterialData materialData, ColorRGBA color);
}