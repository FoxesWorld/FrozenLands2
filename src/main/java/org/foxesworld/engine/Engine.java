package org.foxesworld.engine;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.config.Config;
import org.foxesworld.engine.providers.material.MaterialProvider;
import org.foxesworld.engine.providers.obj.OBJImporter;
import org.foxesworld.engine.providers.sound.SoundProvider;
import org.foxesworld.engine.sky.Sky;
import org.foxesworld.engine.terrain.TerrainGen;

public class Engine extends BaseAppState {

    private Config config;
    private final String configFiles;
    private final FrozenLands frozenLands;
    private final Sky sky;
    protected MaterialProvider materialProvider;
    protected TerrainGen terrainGen;
    protected SoundProvider soundProvider;

    public  Engine(FrozenLands frozenLands, String configFiles) {
        this.frozenLands = frozenLands;
        this.configFiles = configFiles;
        this.getFrozenLands().getAssetManager().registerLoader(OBJImporter.class, "obj");
        this.materialProvider = new MaterialProvider(this.frozenLands);
        this.materialProvider.loadMaterials("materials.json");
        this.terrainGen = new TerrainGen(this);

        this.soundProvider = new SoundProvider(this.frozenLands);
        this.soundProvider.loadSounds("sounds.json");
        this.sky = new Sky(this.frozenLands);
        sky.addSky();
    }

    @Override
    protected void initialize(Application app) {
        this.config = new Config(this);
        this.soundProvider.playSound("player", "walking");
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public MaterialProvider getMaterialProvider() {
        return materialProvider;
    }

    public String[] getConfigFiles() {
        return configFiles.split(",");
    }

    public Config getConfig() {
        return config;
    }

    public FrozenLands getFrozenLands() {
        return frozenLands;
    }

    public TerrainGen getTerrainGen() {
        return terrainGen;
    }
}
