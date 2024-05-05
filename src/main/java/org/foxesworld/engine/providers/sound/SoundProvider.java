package org.foxesworld.engine.providers.sound;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import org.foxesworld.FrozenLands;

import java.util.*;

import static org.foxesworld.engine.utils.InputReader.inputReader;

public class SoundProvider {
    private FrozenLands kernel;
    private Map<String, Map<String, List<AudioNode>>> soundNodes;

    public SoundProvider(FrozenLands kernel) {
        this.kernel = kernel;
        this.soundNodes = new HashMap<>();
    }

    public void loadSounds(String path) {
        Gson gson = new Gson();
        String soundsJson = inputReader(path);
        Map<String, List<SoundEvent>> soundEvents = gson.fromJson(soundsJson, new TypeToken<Map<String, List<SoundEvent>>>(){}.getType());

        for (Map.Entry<String, List<SoundEvent>> entry : soundEvents.entrySet()) {
            Map<String, List<AudioNode>> eventMap = new HashMap<>();
            for (SoundEvent soundEvent : entry.getValue()) {
                List<AudioNode> audioNodes = new ArrayList<>();
                for (String soundFile : soundEvent.getSounds()) {
                    String soundPath = entry.getKey() + '/' + soundEvent.getSoundDir() + soundEvent.getEvent() + '/' + soundFile;
                    SoundSettings settings = soundEvent.getSettings();
                    AudioNode audioNode = createAudioNode(soundPath, settings);
                    audioNodes.add(audioNode);
                }
                eventMap.put(soundEvent.getEvent(), audioNodes);
            }
            soundNodes.put(entry.getKey(), eventMap);
        }

        FrozenLands.logger.info("Finished loading sounds.");
    }

    public AudioNode createAudioNode(String soundPath, SoundSettings settings) {
        String filePath = "sounds/" + soundPath;
        AssetManager assetManager = kernel.getAssetManager();

        AudioData.DataType dataType = AudioData.DataType.valueOf(settings.getDataType());
        AudioNode audioNode = new AudioNode(assetManager, filePath, dataType);

        // Устанавливаем настройки звука
        if (settings.getVolume() != null) {
            audioNode.setVolume(settings.getVolume());
        }
        if (settings.isPositional() != null) {
            audioNode.setPositional(settings.isPositional());
        }
        if (settings.getPitch() != null) {
            audioNode.setPitch(settings.getPitch());
        }

        return audioNode;
    }

    public void playSound(String category, String event) {
        Map<String, List<AudioNode>> eventMap = soundNodes.get(category);
        if (eventMap != null) {
            List<AudioNode> audioNodes = eventMap.get(event);
            if (audioNodes != null && !audioNodes.isEmpty()) {
                AudioNode randomAudioNode = getRandomAudioNode(audioNodes);
                randomAudioNode.play();
                return;
            }
        }
        FrozenLands.logger.warn("Sound event not found: " + category + "." + event);
    }

    private AudioNode getRandomAudioNode(List<AudioNode> audioNodes) {
        Random random = new Random();
        int randomIndex = random.nextInt(audioNodes.size());
        return audioNodes.get(randomIndex);
    }

    static class SoundEvent {
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

    static class SoundSettings {
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
}
