package net.brutewars.sandbox.world;

import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import lombok.SneakyThrows;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.lastlocation.LastLocation;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.bonus.BonusChest;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public final class WorldFactory {
    private final BWorldPlugin plugin;

    public WorldFactory(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void create(final BWorld bWorld, WorldType worldType) {
        bWorld.setLoadingPhase(LoadingPhase.CREATING);

        Executor.create().async(plugin, () -> {
            Logging.debug(plugin, "Creating world for " + bWorld.getAlias() + ": " + bWorld.getWorldName());
            return importWorld(bWorld, worldType);
        }).sync(plugin, (asyncWorld) -> {
            final Location spawnLoc = asyncWorld.getSpawnLocation();
            int xCord = spawnLoc.getBlockX();
            int zCord = spawnLoc.getBlockZ();

            asyncWorld.setSpawnLocation(xCord, asyncWorld.getHighestBlockYAt(spawnLoc) + 1, zCord);
            asyncWorld.getWorldBorder().setCenter(spawnLoc);
            bWorld.setDefaultLocation(new LastLocation(asyncWorld.getSpawnLocation()));

            final World world = asyncWorld.getBukkitWorld();
            zCord += 2;

            // spawn the bonus chest
            new BonusChest(plugin).spawn(new Location(world, xCord, asyncWorld.getHighestBlockYAt(xCord, zCord) + 1, zCord));

            xCord = xCord + 1;

            // spawn torch
            new Location(world, xCord, asyncWorld.getHighestBlockYAt(xCord, zCord) + 1, zCord).getBlock().setType(Material.TORCH);

            Lang.WORLD_CREATED.send(bWorld.getOwner(), bWorld.getWorldSize().getValue());
        });

    }

    public void load(final BWorld bWorld) {
        bWorld.setLoadingPhase(LoadingPhase.LOADING);
        Executor.async(plugin, (unused) -> {
            Logging.debug(plugin, "Loading world for " + bWorld.getAlias() + ": " + bWorld.getWorldName());
            importWorld(bWorld, null);
            Lang.WORLD_LOADED.send(bWorld);
        });
    }

    @SneakyThrows
    public void delete(final BWorld bWorld) {
        Logging.debug(plugin, "Deleting world for " + bWorld.getAlias());
        final File file = new File(bWorld.getWorldName());
        plugin.getServer().unloadWorld(getWorld(bWorld).getNow(null), false);
        bWorld.setLoadingPhase(LoadingPhase.UNLOADED);
        FileUtils.deleteDirectory(file);
    }

    public void unload(final BWorld bWorld, boolean save) {
        Executor.create().async(plugin, unused -> {
            if (!save) return;
            Logging.debug(plugin, "Saving world for " + bWorld.getAlias());
            getWorld(bWorld).whenComplete((world, throwable) -> world.save());
        }).sync(plugin, unused -> {
            Logging.debug(plugin, "Unloaded world for " + bWorld.getAlias());
            plugin.getServer().unloadWorld(bWorld.getWorldName(), false);
            bWorld.setLoadingPhase(LoadingPhase.UNLOADED);
        });
    }

    @SneakyThrows
    public void setWorldBorder(final BWorld bWorld, final WorldSize worldSize) {
        getWorld(bWorld).getNow(null).getWorldBorder().setSize(worldSize.getValue());
    }

    public CompletableFuture<World> getWorld(final BWorld bWorld) {
        final CompletableFuture<World> cf = new CompletableFuture<>();

        if (!bWorld.getLoadingPhase().equals(LoadingPhase.UNLOADED)) {
            cf.complete(plugin.getServer().getWorld(bWorld.getWorldName()));
        } else {
            Executor.async(plugin, unused -> {
                importWorld(bWorld, null);
                cf.complete(plugin.getServer().getWorld(bWorld.getWorldName()));
            });
        }

        return cf;
    }

    private AsyncWorld importWorld(final BWorld bWorld, final WorldType worldType) {
        final WorldCreator wc = new WorldCreator(bWorld.getWorldName());
        if (worldType != null)
            wc.type(worldType);
        final AsyncWorld world = AsyncWorld.create(wc);
        world.setKeepSpawnInMemory(false);
        world.getWorldBorder().setSize(bWorld.getWorldSize().getValue());

        world.commit();
        bWorld.setLoadingPhase(LoadingPhase.LOADED);
        return world;
    }

}