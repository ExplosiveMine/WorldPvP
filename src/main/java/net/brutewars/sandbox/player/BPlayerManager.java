package net.brutewars.sandbox.player;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.entity.Player;

import java.util.*;

public final class BPlayerManager {
    private final BWorldPlugin plugin;

    private final Map<UUID, BPlayer> players = new HashMap<>();

    public BPlayerManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public BPlayer createBPlayer(UUID uuid) {
        return createBPlayer(uuid, null);
    }

    public BPlayer createBPlayer(UUID uuid, BWorld bWorld) {
        BPlayer bPlayer = new BPlayer(plugin, uuid, bWorld);
        players.put(uuid, bPlayer);
        return bPlayer;
    }

    public BPlayer getBPlayer(UUID uuid) {
        return containsBPlayer(uuid) ? players.get(uuid) : createBPlayer(uuid);
    }

    public BPlayer getBPlayer(Player player) {
        return getBPlayer(player.getUniqueId());
    }

    public boolean containsBPlayer(UUID uuid) {
        return players.containsKey(uuid);
    }

    public Collection<BPlayer> getBPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

}