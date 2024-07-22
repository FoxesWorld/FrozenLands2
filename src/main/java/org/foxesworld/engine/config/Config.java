package org.foxesworld.engine.config;

import com.google.gson.GsonBuilder;
import org.foxesworld.FrozenLands;
import org.foxesworld.cfgProvider.CfgProvider;
import org.foxesworld.engine.Engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Config extends org.foxesworld.engine.config.abs.Config {

    public Config(Engine engine) {
        setCfgExportDir("config");
        setDirPathIndex(3);
        setCfgFileExtension(".json");
        CfgProvider.setDefaultConfFilesDir("config/");
        CfgProvider.setLOGGER(FrozenLands.logger);
        addCfgFiles(engine.getConfigFiles());
        this.CONFIG = getCfgMaps().get("config");
    }
    @Override
    public void addToConfig(Map<String, String> inputData, List values) {
        for (Map.Entry<String, String> configEntry : inputData.entrySet()) {
            if (values.contains(configEntry.getKey())) {
                this.getCONFIG().put(configEntry.getKey(), configEntry.getValue());
            }
        }
    }
    @Override
    public void setConfigValue(String key, Object value){
        if(CONFIG.get(key) != null) {
            clearConfigData(Collections.singletonList(key), false);
        }
        CONFIG.put(key, value);
    }

    @Override
    public void clearConfigData(List<String> dataToClear, boolean write) {
        FrozenLands.logger.debug("Wiping "+dataToClear);
        for (String keyToWipe : dataToClear) {
            this.CONFIG.remove(keyToWipe);
        }
        if (write) {
            this.writeCurrentConfig();
        }
    }
    @Override
    public void clearConfigData(String dataToClear, boolean write) {
        FrozenLands.logger.debug("Wiping "+dataToClear);
        this.CONFIG.remove(dataToClear);
        if (write) {
            this.writeCurrentConfig();
        }
    }

    @Override
    public void writeCurrentConfig() {
        FrozenLands.logger.debug("Writing Config");
        try (FileWriter fileWriter = new FileWriter(getFullPath() + File.separator + "config/config.json")) {
            fileWriter.write(configToJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Map<String, Map<String, Object>> getCfgMaps() {
        return getAllCfgMaps();
    }
    public String configToJSON() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(CONFIG);
    }
    public Map<String, Object> getCONFIG() {
        return CONFIG;
    }
}