package net.brutewars.sandbox.bworld;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.world.WorldFactory;

import java.util.*;

public final class BWorldManager {
    private final BWorldPlugin plugin;

    private final Map<UUID, BWorld> bWorlds;

    @Getter private final WorldFactory worldFactory;

    public final long INVITING_TIME;
    public final long UNLOADING_TIME;
    public final long RESETTING_TIME;

    public BWorldManager(final BWorldPlugin plugin) {
        this.plugin = plugin;
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

        final BWorld bWorld = new BWorld(plugin, uuid, owner);

        owner.setBWorld(bWorld);

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

        bWorld.getPlayers(true).forEach(bPlayer -> {
            bPlayer.sendToSpawn();
            bPlayer.removeBWorld(bWorld);
        });

        worldFactory.delete(bWorld);
        bWorlds.remove(bWorld.getUuid());
    }

    public BWorld getBWorld(UUID uuid) {
        return bWorlds.get(uuid);
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

}