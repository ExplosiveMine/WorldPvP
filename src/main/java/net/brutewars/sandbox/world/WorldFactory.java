package net.brutewars.sandbox.world;

import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import lombok.SneakyThrows;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.lastlocation.LastLocation;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.bonus.BonusChest;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

public final class WorldFactory {
    private final BWorldPlugin plugin;
    private final String PATH;

    private final BonusChest bonusChest;

    public WorldFactory(final BWorldPlugin plugin) {
        this.plugin = plugin;
        this.bonusChest = new BonusChest(plugin);
        this.PATH = plugin.getDataFolder() + File.separator + "worlds" + File.separator;
    }

    public void create(final BWorld bWorld) {
        bWorld.setLoadingPhase(LoadingPhase.CREATING);

        Executor.create().async(plugin, () -> {
            Logging.debug(plugin, "Creating world for " + bWorld.getAlias() + ": " + bWorld.getWorldName());

            final AsyncWorld asyncWorld = importWorld(bWorld);

            // set default location
            final Location spawnLocation = asyncWorld.getSpawnLocation();
            final int xCord = spawnLocation.getBlockX();
            final int zCord = spawnLocation.getBlockZ();
            asyncWorld.setSpawnLocation(xCord, asyncWorld.getHighestBlockYAt(xCord, zCord) + 1, zCord);
            asyncWorld.getWorldBorder().setCenter(asyncWorld.getSpawnLocation());
            bWorld.setDefaultLocation(new LastLocation(asyncWorld.getSpawnLocation()));

            return asyncWorld;
        }).sync(plugin, (asyncWorld) -> {
            final World world = asyncWorld.getBukkitWorld();
            int xCord = asyncWorld.getSpawnLocation().getBlockX();
            final int zCord = asyncWorld.getSpawnLocation().getBlockZ() + 2;

            // spawn the bonus chest
            bonusChest.spawn(new Location(world, xCord, asyncWorld.getHighestBlockYAt(xCord, zCord) + 1, zCord));

            xCord = xCord + 1;
            // spawn torch
            new Location(world, xCord, asyncWorld.getHighestBlockYAt(xCord, zCord) + 1, zCord).getBlock().setType(Material.TORCH);

            Lang.WORLD_CREATED.send(bWorld.getOwner(), bWorld.getWorldSize().getValue());
        });

    }

    public void load(final BWorld bWorld) {
        bWorld.setLoadingPhase(LoadingPhase.LOADING);
        Executor.async(plugin, unused -> {
            Logging.debug(plugin, "Loading world for " + bWorld.getAlias() + ": " + bWorld.getWorldName());
            importWorld(bWorld);
            Lang.WORLD_LOADED.send(bWorld);
        });
    }

    @SneakyThrows(IOException.class)
    public void delete(final BWorld bWorld) {
        Logging.debug(plugin, "Deleting world for " + bWorld.getAlias());
        final File file = new File(PATH + bWorld.getWorldName());
        plugin.getServer().unloadWorld(getWorld(bWorld), false);
        bWorld.setLoadingPhase(LoadingPhase.UNLOADED);
        FileUtils.deleteDirectory(file);
    }

    public void unload(final BWorld bWorld, boolean save) {
        Executor.create().async(plugin, unused -> {
            if (!save) return;
            Logging.debug(plugin, "Saving world for " + bWorld.getAlias());
            AsyncWorld.wrap(getWorld(bWorld)).save();
        }).sync(plugin, unused -> {
            Logging.debug(plugin, "Unloaded world for " + bWorld.getAlias());
            plugin.getServer().unloadWorld(bWorld.getWorldName(), false);
            bWorld.setLoadingPhase(LoadingPhase.UNLOADED);
        });
    }

    public void setWorldBorder(final BWorld bWorld, final WorldSize worldSize) {
        getWorld(bWorld).getWorldBorder().setSize(worldSize.getValue());
    }

    public World getWorld(final BWorld bWorld) {
        return plugin.getServer().getWorld(PATH + bWorld.getWorldName());
    }

    private AsyncWorld importWorld(final BWorld bWorld) {
        final AsyncWorld world = AsyncWorld.create(new WorldCreator(PATH + bWorld.getWorldName()));
        world.setKeepSpawnInMemory(false);
        world.getWorldBorder().setSize(bWorld.getWorldSize().getValue());

        world.commit();
        bWorld.setLoadingPhase(LoadingPhase.LOADED);

        return world;
    }

}