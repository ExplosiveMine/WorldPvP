package net.brutewars.worldpvp.world;

import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import lombok.SneakyThrows;
import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.thread.Executor;
import net.brutewars.worldpvp.utils.Logging;
import org.apache.commons.io.FileUtils;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public final class WorldFactory {
    private final BWorldPlugin plugin;

    public WorldFactory(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void create(final BWorld bWorld) {
        bWorld.setWorldPhase(WorldPhase.CREATING);
        Executor.async(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                Logging.debug(plugin, "Creating world for " + bWorld.getOwner().getName() + ": " + bWorld.getWorldName());

                final AsyncWorld world = AsyncWorld.create(new WorldCreator(bWorld.getWorldName()));
                world.setKeepSpawnInMemory(false);
                world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0) + 2, 0);
                world.getWorldBorder().setCenter(world.getSpawnLocation());
                world.getWorldBorder().setSize(bWorld.getWorldSize().getValue());
                world.commit();

                bWorld.setWorldPhase(WorldPhase.LOADED);
                Lang.WORLD_CREATED.send(bWorld.getOwner(), bWorld.getWorldSize().getValue());
            }
        });
    }

    public void load(final BWorld bWorld) {
        Executor.async(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                Logging.debug(plugin, "Loading world for " + bWorld.getOwner().getName() + ": " + bWorld.getWorldName());

                final AsyncWorld world = AsyncWorld.create(new WorldCreator(bWorld.getWorldName()));
                world.getWorldBorder().setSize(bWorld.getWorldSize().getValue());
                world.commit();

                bWorld.setWorldPhase(WorldPhase.LOADED);
                Lang.WORLD_LOADED.send(bWorld);
            }
        });
    }

    @SneakyThrows(IOException.class)
    public void delete(final BWorld bWorld) {
        Logging.debug(plugin, "Deleting world for " + bWorld.getOwner().getName());

        final File file = plugin.getServer().getWorld(bWorld.getWorldName()).getWorldFolder();
        plugin.getServer().unloadWorld(bWorld.getWorldName(), false);
        bWorld.setWorldPhase(WorldPhase.UNLOADED);
        FileUtils.deleteDirectory(file);
    }

    public void unload(final BWorld bWorld, boolean save) {
        Executor.asyncThenSync(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                if (save) {
                    Logging.debug(plugin, "Saving world for " + bWorld.getOwner().getName());
                    AsyncWorld.wrap(plugin.getServer().getWorld(bWorld.getWorldName())).save();
                }
            }
        }, unused -> {
            Logging.debug(plugin, "Unloaded world for " + bWorld.getOwner().getName());
            plugin.getServer().unloadWorld(bWorld.getWorldName(), false);
            bWorld.setWorldPhase(WorldPhase.UNLOADED);
        });

    }

    public void setWorldBorder(final String worldName, final WorldSize worldSize) {
        plugin.getServer().getWorld(worldName).getWorldBorder().setSize(worldSize.getValue());
    }

}