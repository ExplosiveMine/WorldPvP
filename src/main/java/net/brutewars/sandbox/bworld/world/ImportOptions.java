package net.brutewars.sandbox.bworld.world;

import lombok.Getter;
import net.brutewars.sandbox.utils.Pair;
import org.bukkit.World;
import org.bukkit.WorldType;

public final class ImportOptions {
    @Getter private WorldType worldType = WorldType.NORMAL;

    private World.Environment environment = World.Environment.NORMAL;
    @Getter private boolean generateSpawn = false;

    @Getter private String name = "";

    public ImportOptions() {
    }

    public ImportOptions setName(String name) {
        this.name = name;
        return this;
    }

    public ImportOptions setWorldType(WorldType worldType) {
        this.worldType = worldType;
        return this;
    }

    public ImportOptions setEnvironment(World.Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * @return the key which was used to select the world in the roster
     * if the world is the NETHER or END dimension, then the worldType is
     * set to {@link WorldType#NORMAL} so that a correct mapping can be found
     * in the world roster and so that it makes sense as there is no flat NETHER
     * or amplified END dimension
     */
    public Pair<WorldType, World.Environment> getPair() {
        return (environment == World.Environment.NORMAL) ? Pair.of(worldType, environment) : Pair.of(WorldType.NORMAL, environment);
    }

    public ImportOptions setGenerateCustomSpawn(boolean generateSpawn) {
        this.generateSpawn = generateSpawn;
        return this;
    }

}