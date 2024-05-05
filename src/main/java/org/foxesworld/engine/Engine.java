package org.foxesworld.engine;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import org.foxesworld.FrozenLands;
import org.foxesworld.engine.providers.material.MaterialProvider;
import org.foxesworld.engine.sky.Sky;

public class Engine extends BaseAppState {

    private final FrozenLands frozenLands;
    private final Sky sky;
    protected MaterialProvider materialProvider;

    public  Engine(FrozenLands frozenLands) {
        this.frozenLands = frozenLands;
        this.materialProvider = new MaterialProvider(this.frozenLands);
        this.materialProvider.loadMaterials("data/materials.json");
        this.sky = new Sky(this.frozenLands);
        sky.addSky();
    }

    @Override
    protected void initialize(Application app) {

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
}
