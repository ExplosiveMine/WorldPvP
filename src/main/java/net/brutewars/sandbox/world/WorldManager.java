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
import org.bukkit.World;
import org.bukkit.WorldType;

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
            preGenWorld(i, WorldType.FLAT);
            preGenWorld(i, WorldType.NORMAL);
            preGenWorld(i, WorldType.AMPLIFIED);
        }

        Logging.debug(plugin, "World roster created in: " + (System.currentTimeMillis() - currentTime) + "ms");
    }

    private void preGenWorld(int i, WorldType worldType) {
        File worldFile = new File(plugin.getDataFolder(), "world-roster" + File.separator + worldType + i);
        if (worldFile.exists())
            return;

        World world = worldFactory.create(worldFile.getPath(), worldType);
        worldFactory.setupWorld(world);
        plugin.getServer().unloadWorld(world, true);
    }

    public File getRandomWorldFile(WorldType worldType) {
        int x = ThreadLocalRandom.current().nextInt(0, plugin.getConfigSettings().getConfigParser().getRosterSize());
        return new File(plugin.getDataFolder(), "world-roster" + File.separator + worldType + x);
    }

    public void create(BWorld bWorld, WorldType worldType) {
        Logging.debug(plugin, "Creating world for " + bWorld.getAlias() + ": " + bWorld.getWorldName());
        bWorld.setLoadingPhase(LoadingPhase.CREATING);

        Executor.create()
                .async(plugin, unused -> worldFactory.importWorld(getRandomWorldFile(worldType), bWorld.getWorldName()))
                .sync(plugin, unused -> setWorldSettings(bWorld));
    }

    public void load(BWorld bWorld) {
        Logging.debug(plugin, "Loading world for " + bWorld.getAlias() + ": " + bWorld.getWorldName());
        bWorld.setLoadingPhase(LoadingPhase.LOADING);
        setWorldSettings(bWorld);
        Lang.WORLD_LOADED.send(bWorld);
    }

    public void setWorldSettings(BWorld bWorld) {
        // we get the world for which the settings need to be updated
        World world = worldFactory.loadWorld(bWorld.getWorldName());

        worldFactory.setWorldBorder(world, bWorld.getWorldSize());
        world.setDifficulty(bWorld.getDifficulty());
        bWorld.setDefaultLocation(new BLocation(world.getSpawnLocation().add(0.5, 0, 0.5)));
        bWorld.setLoadingPhase(LoadingPhase.LOADED);
    }

    public void delete(BWorld bWorld) {
        Logging.debug(plugin, "Deleting world for " + bWorld.getAlias());

        if (!bWorld.getLoadingPhase().equals(LoadingPhase.UNLOADED))
            unload(bWorld, false);

        deletedWorlds.add(bWorld.getWorldName());
    }

    public void unload(BWorld bWorld, boolean save) {
        if (save)
            Logging.debug(plugin, "Saving world for " + bWorld.getAlias());

        Logging.debug(plugin, "Unloaded world for " + bWorld.getAlias());
        plugin.getServer().unloadWorld(bWorld.getWorldName(), save);
        bWorld.setLoadingPhase(LoadingPhase.UNLOADED);
    }

    public World getWorld(BWorld bWorld) {
        if (bWorld.getLoadingPhase().equals(LoadingPhase.UNLOADED))
            load(bWorld);

        return plugin.getServer().getWorld(bWorld.getWorldName());
    }

    public void eraseDeletedWorlds() {
        for (String deletedWorld : deletedWorlds)
            FileUtils.deleteDirectory(new File(deletedWorld));
    }

}