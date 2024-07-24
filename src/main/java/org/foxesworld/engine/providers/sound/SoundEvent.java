package org.foxesworld.engine.providers.sound;

import java.util.List;

public class SoundEvent {
    private String event;
    private String soundDir;
    private SoundSettings settings;
    private List<String> sounds;

    public String getEvent() {
        return event;
    }

    public String getSoundDir() {
        return soundDir;
    }

    public SoundSettings getSettings() {
        return settings;
    }

    public List<String> getSounds() {
        return sounds;
    }
}