package net.brutewars.worldpvp.world;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.rank.RankManager;
import net.brutewars.worldpvp.utils.Logging;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public final class BWorldManager {
    private final BWorldPlugin plugin;

    private final Map<UUID, BWorld> bWorlds = new HashMap<>();

    @Getter private final WorldFactory worldFactory;

    public final long INVITING_TIME;
    public final long UNLOADING_TIME;
    public final long RESETTING_TIME;

    public BWorldManager(final BWorldPlugin plugin) {
        this.plugin = plugin;
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
        owner.setRank(RankManager.getOwner());

        bWorlds.put(uuid, bWorld);

        if (newBWorld)
            worldFactory.create(bWorld);

        return bWorld;
    }

    public void removeBWorld(BWorld bWorld) {
        if (bWorld.getResetting() != -1) {
            Logging.debug(plugin, "Resetting: " + bWorld.getOwner().getName());
            plugin.getServer().getScheduler().cancelTask(bWorld.getResetting());
        }

        bWorld.getPlayers(true).forEach(bPlayer -> {
            bPlayer.sendToSpawn();
            bPlayer.setBWorld(null);
            bPlayer.setRank(null);
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

    public World getWorld(final BWorld bWorld) {
        return plugin.getServer().getWorld(bWorld.getWorldName());
    }

    public UUID generateNextUuid() {
        UUID uuid;

        do {
            uuid = UUID.randomUUID();
        } while (getBWorld(uuid) != null);

        return uuid;
    }

}