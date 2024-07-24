package org.foxesworld.engine.providers.sound;

public class SoundSettings {
    private Float volume;
    private Boolean positional;
    private Float pitch;
    private String dataType;

    public Float getVolume() {
        return volume;
    }

    public Boolean isPositional() {
        return positional;
    }

    public Float getPitch() {
        return pitch;
    }

    public String getDataType() {
        return dataType;
    }
}