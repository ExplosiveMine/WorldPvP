package net.brutewars.sandbox.bworld;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.WorldFactory;
import org.bukkit.World;

import java.io.File;
import java.util.*;

public final class BWorldManager {
    private final BWorldPlugin plugin;

    @Getter private final SpawnBWorld spawn;

    private final Map<UUID, BWorld> bWorlds;

    @Getter private final WorldFactory worldFactory;

    public final long INVITING_TIME;
    public final long UNLOADING_TIME;
    public final long RESETTING_TIME;

    public BWorldManager(final BWorldPlugin plugin) {
        this.plugin = plugin;
        this.spawn = new SpawnBWorld(plugin);
        this.bWorlds = new HashMap<>();
        this.worldFactory = new WorldFactory(plugin);

        this.INVITING_TIME = plugin.getConfig().getLong("invites.expire time");
        this.RESETTING_TIME = plugin.getConfig().getLong("world.resetting time");
        this.UNLOADING_TIME = plugin.getConfig().getLong("world.unloading time");
    }

    public void createBWorld(BPlayer owner) {
        Preconditions.checkNotNull(owner, "owner parameter cannot be null");

        UUID uuid = generateNextUuid();
        loadBWorld(uuid, owner, true);
    }

    public BWorld loadBWorld(final UUID uuid, BPlayer owner, boolean newBWorld) {
        Preconditions.checkNotNull(uuid, "uuid parameter cannot be null");
        Preconditions.checkNotNull(owner, "owner parameter cannot be null");

        final BWorld bWorld = new BWorld(plugin, uuid);
        bWorld.init(owner);

        bWorlds.put(uuid, bWorld);

        if (newBWorld)
            worldFactory.create(bWorld);

        return bWorld;
    }

    public void removeBWorld(BWorld bWorld) {
        if (bWorld.getResetting() != -1) {
            Logging.debug(plugin, "Resetting: " + bWorld.getAlias());
            plugin.getServer().getScheduler().cancelTask(bWorld.getResetting());
        }

        // teleport visitors in that world to spawn
        final World world = worldFactory.getWorld(bWorld);
        // this is the same as !bWorld.getLoadingPhase().equals(LoadingPhase.LOADED) which is consistent but this is safer and more direct
        if (world != null) {
            worldFactory.getWorld(bWorld).getPlayers().stream()
                    .map(player -> plugin.getBPlayerManager().getBPlayer(player))
                    .filter(bPlayer -> !bPlayer.isInBWorld(bWorld, false))
                    .forEach(bPlayer -> {
                        Lang.ON_RESET_VISITOR.send(bPlayer);
                        getSpawn().teleportToWorld(bPlayer);
                    });
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
        final String[] a = worldPath.split("worlds" + File.separator);
        if (a.length != 2)
            return null;
        return getBWorld(UUID.fromString(a[1]));
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