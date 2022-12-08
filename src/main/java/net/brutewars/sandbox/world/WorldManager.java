package net.brutewars.sandbox.world;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.FileUtils;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import net.brutewars.sandbox.world.holograms.RainbowHologram;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class WorldManager {
    private final BWorldPlugin plugin;

    @Getter
    private final WorldFactory worldFactory;

    @Getter @Setter
    private long lastRoster;

    private final Set<String> deletedWorlds = new HashSet<>();

    public WorldManager(BWorldPlugin plugin) {
        this.plugin = plugin;
        this.worldFactory = new WorldFactory(plugin);
    }

    public void setupWorldRoster() {
        File rosterFolder = new File(plugin.getDataFolder(), "world-roster");

        long currentTime = System.currentTimeMillis();
        if (currentTime >= lastRoster + plugin.getConfigSettings().getConfigParser().getWorldRenewTime() * 1000)
            FileUtils.deleteDirectory(rosterFolder);

        if (!rosterFolder.exists()) {
            rosterFolder.mkdirs();
            setLastRoster(currentTime);
        }

        for (int i = 0; i <  plugin.getConfigSettings().getConfigParser().getRosterSize(); i++) {
            preGenWorld(i, WorldType.FLAT, World.Environment.NORMAL);
            preGenWorld(i, WorldType.NORMAL, World.Environment.NORMAL);
            preGenWorld(i, WorldType.AMPLIFIED, World.Environment.NORMAL);

            preGenWorld(i, WorldType.NORMAL, World.Environment.NETHER);
            preGenWorld(i, WorldType.NORMAL, World.Environment.THE_END);
        }

        Logging.debug(plugin, "World roster created in: " + (System.currentTimeMillis() - currentTime) + "ms");
    }

    private void preGenWorld(int i, WorldType worldType, World.Environment env) {
        String fileName = "world-roster" + File.separator;
        if (env == World.Environment.NORMAL)
            fileName = fileName + worldType;
        else
            fileName = fileName + env;

        fileName = fileName + i;

        File worldFile = new File(plugin.getDataFolder(), fileName);
        if (worldFile.exists())
            return;

        World world = worldFactory.create(worldFile.getPath(), worldType, env);
        worldFactory.setupWorld(world);
        plugin.getServer().unloadWorld(world, true);
    }

    public File getRandomWorldFile(WorldType worldType, World.Environment env) {
        String name;
        if (env == World.Environment.NORMAL)
            name = worldType.toString();
        else
            name = env.toString();

        int x = ThreadLocalRandom.current().nextInt(0, plugin.getConfigSettings().getConfigParser().getRosterSize());
        return new File(plugin.getDataFolder(), "world-roster" + File.separator + name + x);
    }

    public void create(BWorld bWorld, WorldType worldType) {
        Logging.debug(plugin, "Creating world for " + bWorld.getAlias() + ": " + bWorld.getWorldPath());

        bWorld.getEnvironments(LoadingPhase.UNLOADED).forEach(env -> Executor.create()
                .async(plugin, unused -> worldFactory.importWorld(getRandomWorldFile(worldType, env), bWorld.getWorldPath() + env))
                .sync(plugin, unused -> {
                    if (env == World.Environment.NORMAL) {
                        bWorld.setWorldPhases(LoadingPhase.CREATING, env);
                        initWorld(bWorld, env);
                    }
                }));
    }

    public void load(BWorld bWorld, World.Environment env) {
        Logging.debug(plugin, "Loading world for " + bWorld.getAlias() + ": " + bWorld.getWorldPath() + env);
        bWorld.setWorldPhases(LoadingPhase.LOADING, env);
        initWorld(bWorld, env);
        Lang.WORLD_LOADED.send(bWorld);
    }

    public void initWorld(BWorld bWorld, World.Environment env) {
        World world = worldFactory.loadWorld(bWorld, env);

        // apply world settings if they have been updated
        worldFactory.setWorldBorder(world, bWorld.getWorldSize());
        world.setDifficulty(bWorld.getDifficulty());
        if (bWorld.isKeepInventory())
            world.setGameRule(GameRule.KEEP_INVENTORY, true);

        // this is the main world
        if (env == World.Environment.NORMAL) {
            Location spawnLoc = world.getSpawnLocation();
            bWorld.setDefaultLocation(new BLocation(spawnLoc.add(0.5, 0, 0.5)));

            Location loc = spawnLoc.clone().add(0, 0, -2);

            String data = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "hologram_welcome", "");
            if (!data.isEmpty())
                plugin.getHologramManager().addDynamicHologram(new RainbowHologram(loc, Lang.WELCOME_HOLOGRAM_1.get()), "welcome");
        }

        bWorld.setWorldPhases(LoadingPhase.LOADED, env);
    }

    public void delete(BWorld bWorld) {
        Logging.debug(plugin, "Deleting world for " + bWorld.getAlias());
        unload(bWorld, false);
        deletedWorlds.add(bWorld.getWorldPath());
    }

    public void unload(BWorld bWorld, boolean save) {
        if (save)
            Logging.debug(plugin, "Saving world for " + bWorld.getAlias());

        Logging.debug(plugin, "Unloaded world for " + bWorld.getAlias());
        bWorld.getLoadedWorlds().forEach(world -> {
            World.Environment env = world.getEnvironment();
            plugin.getServer().unloadWorld(bWorld.getWorldPath() + env, save);
            bWorld.setWorldPhases(LoadingPhase.UNLOADED, env);
        });
    }

    public World getWorld(BWorld bWorld, World.Environment env) {
        if (bWorld.getWorldPhase(env) == LoadingPhase.UNLOADED)
            load(bWorld, env);

        return plugin.getServer().getWorld(bWorld.getWorldPath() + env.toString());
    }

    public void eraseDeletedWorlds() {
        for (String deletedWorld : deletedWorlds)
            FileUtils.deleteDirectory(new File(deletedWorld));
    }

}