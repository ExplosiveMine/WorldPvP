package net.brutewars.sandbox.world;

import lombok.SneakyThrows;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.utils.FileUtils;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.dimensions.Dimension;
import net.brutewars.sandbox.world.dimensions.EndDimension;
import net.brutewars.sandbox.world.dimensions.NetherDimension;
import net.brutewars.sandbox.world.dimensions.OverworldDimension;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;

import java.io.File;


public final class WorldFactory {
    private final BWorldPlugin plugin;

    public WorldFactory(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @param worldName name of the world to be created
     * @param worldType if set to null, the method assumes the world only needs to be loaded
     *                  but will still create the world if it does not exist
     * @param env       the world's dimension
     * @return The created/loaded world
     */
    public World create(String worldName, WorldType worldType, World.Environment env) {
        WorldCreator wc = new WorldCreator(worldName);
        if (worldType != null) {
            Logging.debug(plugin, "Generating new world: " + worldName);
            wc.type(worldType);
        } else {
            wc.keepSpawnLoaded(TriState.FALSE);
        }

        wc.environment(env);
        return plugin.getServer().createWorld(wc);
    }

    public void setWorldBorder(World world, WorldSize worldSize) {
        world.getWorldBorder().setSize(worldSize.getValue());
    }

    @SneakyThrows
    public void importWorld(File rosterFile, String worldName) {
        File worldFile = new File(worldName);

        if (!worldFile.exists()) {
            Logging.debug(plugin, "Importing world file " + worldName);
            FileUtils.copyDirectory(rosterFile, worldFile, s -> !s.equals("uid.dat"));
        }
    }

    public World loadWorld(BWorld bWorld, World.Environment env) {
        return create(bWorld.getWorldPath() + env, null, env);
    }

    public void setupWorld(World world) {
        World.Environment env = world.getEnvironment();
        Location spawnLoc = world.getSpawnLocation();

        spawnLoc = getDimension(env).generate(spawnLoc);

        world.setSpawnLocation(spawnLoc);
        world.getWorldBorder().setCenter(spawnLoc);
    }

    public Dimension getDimension(World.Environment env) {
        switch (env) {
            case THE_END -> {
                return new EndDimension(plugin);
            }
            case NETHER -> {
                return new NetherDimension(plugin);
            }
            default -> {
                return new OverworldDimension(plugin);
            }
        }
    }

}