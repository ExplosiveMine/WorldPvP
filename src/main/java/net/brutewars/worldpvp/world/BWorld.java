package net.brutewars.worldpvp.world;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.rank.Rank;
import net.brutewars.worldpvp.thread.Executor;
import net.brutewars.worldpvp.utils.Logging;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public final class BWorld {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;
    @Getter private final BPlayer owner;

    private final Set<BPlayer> players = new HashSet<>();
    @Getter private final Map<BPlayer, BPlayer> invitedPlayers = new HashMap<>();

    @Getter private WorldSize worldSize;

    @Getter @Setter private int resetting = -1;
    @Getter @Setter private int unloading = -1;


    @Getter @Setter private WorldPhase worldPhase = WorldPhase.UNLOADED;

    public BWorld(final BWorldPlugin plugin, UUID uuid, BPlayer owner) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.owner = owner;
        this.worldSize = WorldSize.DEFAULT;

        updateWorldSize();
    }

    public Set<BPlayer> getOnlineBPlayers() {
        return getPlayers(true).stream().filter(BPlayer::isOnline).collect(Collectors.toSet());
    }

    public Set<BPlayer> getPlayers(boolean includeOwner) {
        final Set<BPlayer> _players = new HashSet<>(players);
        if (includeOwner)
            _players.add(owner);
        return _players;
    }

    public void addPlayer(BPlayer bPlayer, Rank rank) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");
        Preconditions.checkNotNull(rank, "rank parameter cannot be null");

        bPlayer.setRank(rank);
        bPlayer.setBWorld(this);

        players.add(bPlayer);
    }

    public void removePlayer(BPlayer bPlayer) {
        Preconditions.checkNotNull(bPlayer, "bPlayer parameter cannot be null");

        bPlayer.setBWorld(null);
        bPlayer.setRank(null);
        bPlayer.sendToSpawn();

        players.remove(bPlayer);
    }

    public boolean isInvited(BPlayer invitee) {
        return invitedPlayers.containsKey(invitee);
    }

    public void invite(BPlayer inviter, BPlayer invitee) {
        invitedPlayers.put(invitee, inviter);
        Executor.syncTimer(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                invitedPlayers.remove(invitee);
            }
        }, plugin.getBWorldManager().INVITING_TIME);
    }

    public BPlayer getInviter(BPlayer invitee) {
        return invitedPlayers.get(invitee);
    }

    public void removeInvite(BPlayer invitee) {
        invitedPlayers.remove(invitee);
    }

    public void initialiseReset() {
        Logging.debug(plugin, "Initialised reset for: " + getOwner().getName());

        setResetting(Executor.syncTimer(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                Logging.debug(plugin, "Reset cancelled for: " + getOwner().getName());
                setResetting(-1);
            }
        }, plugin.getBWorldManager().RESETTING_TIME));
    }

    public void updateWorldSize() {
        WorldSize maxSize = WorldSize.DEFAULT;

        for (WorldSize worldSize : WorldSize.values()) {
            if (getOwner().hasPermission(worldSize.getPermission()) && worldSize.getValue() > maxSize.getValue())
                maxSize = worldSize;
        }

        if (worldSize.equals(maxSize))
            return;

        Logging.debug(plugin, "Updated WorldSize for: " + getOwner().getName());

        if (worldPhase.equals(WorldPhase.LOADED)) {
            Lang.WORLD_BORDER_UPDATE.send(this, (maxSize.getValue() > worldSize.getValue() ? "increased" : "decreased"),worldSize.getValue(), maxSize.getValue());
            plugin.getBWorldManager().getWorldFactory().setWorldBorder(getWorldName(), maxSize);
        }

        this.worldSize = maxSize;
    }

    public void initialiseUnloading() {
        Logging.debug(plugin, "Initialised unloading for: " + getOwner().getName());

        setUnloading(Executor.syncTimer(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                if (getOnlineBPlayers().size() != 0) return;
                Logging.debug(plugin, "Unloading world: " + getOwner().getName());
                plugin.getBWorldManager().getWorldFactory().unload(BWorld.this, true);
            }
        }, plugin.getBWorldManager().UNLOADING_TIME));
    }

    public void cancelUnloading() {
        Logging.debug(plugin, "Unloading cancelled for: " + getOwner().getName());
        plugin.getServer().getScheduler().cancelTask(getUnloading());
    }

    public String getWorldName() {
        return uuid.toString();
    }

}