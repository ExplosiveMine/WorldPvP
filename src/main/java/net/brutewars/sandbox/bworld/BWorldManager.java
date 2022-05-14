package net.brutewars.sandbox.bworld;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.LoadingPhase;
import net.brutewars.sandbox.world.WorldFactory;
import org.bukkit.WorldType;

import java.io.File;
import java.util.*;

public final class BWorldManager {
    private final BWorldPlugin plugin;

    @Getter private final SpawnBWorld spawn;

    private final Map<UUID, BWorld> bWorlds;

    @Getter private final WorldFactory worldFactory;

    public BWorldManager(final BWorldPlugin plugin) {
        this.plugin = plugin;
        this.spawn = new SpawnBWorld(plugin);
        this.bWorlds = new HashMap<>();
        this.worldFactory = new WorldFactory(plugin);
    }

    public void createBWorld(BPlayer owner, WorldType worldType) {
        loadBWorld(generateNextUuid(), owner, worldType);
    }

    public BWorld loadBWorld(final UUID uuid, BPlayer owner, WorldType worldType) {
        Preconditions.checkNotNull(uuid, "uuid parameter cannot be null");
        Preconditions.checkNotNull(owner, "owner parameter cannot be null");

        final BWorld bWorld = new BWorld(plugin, uuid);
        bWorld.init(owner);

        bWorlds.put(uuid, bWorld);

        if (worldType != null)
            worldFactory.create(bWorld, worldType);

        return bWorld;
    }

    public void removeBWorld(BWorld bWorld) {
        if (bWorld.getResetting() != -1) {
            Logging.debug(plugin, "Resetting: " + bWorld.getAlias());
            plugin.getServer().getScheduler().cancelTask(bWorld.getResetting());
        }

        // teleport visitors in that world to spawn if its loaded
        if (!bWorld.getLoadingPhase().equals(LoadingPhase.UNLOADED)) {
            worldFactory.getWorld(bWorld).whenComplete((world, throwable) -> world.getPlayers().stream()
                    .map(player -> plugin.getBPlayerManager().getBPlayer(player))
                    .filter(bPlayer -> !bPlayer.isInBWorld(bWorld, false))
                    .forEach(bPlayer -> {
                        Lang.ON_RESET_VISITOR.send(bPlayer);
                        getSpawn().teleportToWorld(bPlayer);
                    }));
        }


        // remove all players
        bWorld.getPlayers(false).forEach(bWorld::removePlayer);

        // set owners bWorld to null
        bWorld.getOwner().setBWorld(null);

        // delete the world
        worldFactory.delete(bWorld);

        // remove team from database
        bWorlds.remove(bWorld.getUuid());
    }

    public BWorld getBWorld(UUID uuid) {
        return bWorlds.get(uuid);
    }

    public BWorld getBWorld(final String worldPath) {
        final String[] str = worldPath.split("worlds" + File.separator);
        if (str.length != 2)
            return null;
        return getBWorld(UUID.fromString(str[1]));
    }

    public Collection<BWorld> getBWorlds() {
        return Collections.unmodifiableCollection(bWorlds.values());
    }

    public UUID generateNextUuid() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (getBWorld(uuid) != null);

        return uuid;
    }

    public void updateLastLocations() {
        plugin.getServer().getWorlds().forEach(world -> {
            final BWorld bWorld = getBWorld(world.getName());
            world.getPlayers().forEach(player -> {
                final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(player);
                if (!bPlayer.isInBWorld(bWorld, true))
                    return;

                bWorld.updateLastLocation(bPlayer, player.getLocation());
            });
        });
    }

}