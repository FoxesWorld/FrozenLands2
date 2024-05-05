package org.foxesworld.engine.config.abs;

import com.google.gson.Gson;
import org.foxesworld.cfgProvider.CfgProvider;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public abstract class Config {

    protected Map<String, Object> CONFIG;
    private String cfgFileExtension = "";

    protected void addCfgFiles(String[] configFiles){
        for(String cfgUnit: configFiles){
            String cfgFileName = cfgUnit + cfgFileExtension;
            new CfgProvider(cfgFileName);
        }
    }

    @SuppressWarnings("unused")
    public abstract void addToConfig(Map<String, String> inputData, List values);
    @SuppressWarnings("unused")
    public abstract void setConfigValue(String key, Object value);
    @SuppressWarnings("unused")
    public abstract void clearConfigData(List<String> dataToClear, boolean write);
    @SuppressWarnings("unused")
    public abstract void clearConfigData(String dataToClear, boolean write);

    @SuppressWarnings("unused")
    public void assignConfigValues(){
        for(Map.Entry<String, Object> configMap : this.CONFIG.entrySet()){
            try {
                Field field = ObjectInputFilter.Config.class.getDeclaredField(configMap.getKey());
                if(field.hashCode()!= 0) {
                    field.set(this, configMap.getValue());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                this.writeCurrentConfig();
            }
        }
    }

    public void writeCurrentConfig() {
        try (FileWriter fileWriter = new FileWriter(getFullPath() + File.separator + "config/config.json")) {
            fileWriter.write(configToJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String configToJSON() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Config.class, new ConfigSerializer())
                .create();
        return gson.toJson(this);
    }

    protected void setDirPathIndex(int index){
        CfgProvider.setBaseDirPathIndex(index);
    }
    protected  void setCfgExportDir(String dir){
        CfgProvider.setCfgExportDirName(dir);
    }
    protected void setCfgFileExtension(String ext){
        this.cfgFileExtension = ext;
        CfgProvider.setCfgFileExtension(ext);
    }
    protected Map<String, Map<String, Object>> getAllCfgMaps(){
        return CfgProvider.getAllCfgMaps();
    }
    public static String getFullPath() {
        return CfgProvider.getGameFullPath() + File.separator;
    }
    public Map<String, Object> getCONFIG() {
        return CONFIG;
    }
}