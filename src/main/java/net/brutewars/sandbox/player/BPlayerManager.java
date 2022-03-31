package net.brutewars.sandbox.player;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.rank.Rank;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.entity.Player;

import java.util.*;

public final class BPlayerManager {
    private final BWorldPlugin plugin;

    private final Map<UUID, BPlayer> players = new HashMap<>();

    public BPlayerManager(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public BPlayer createBPlayer(UUID uuid) {
        return createBPlayer(uuid, null, null);
    }

    public BPlayer createBPlayer(UUID uuid, BWorld bWorld, Rank rank) {
        final BPlayer bPlayer = new BPlayer(plugin, uuid, bWorld, rank);
        players.put(uuid, bPlayer);
        return bPlayer;
    }

    public BPlayer getBPlayer(final UUID uuid) {
        return containsBPlayer(uuid) ? players.get(uuid) : createBPlayer(uuid);
    }

    public BPlayer getBPlayer(final Player player) {
        return getBPlayer(player.getUniqueId());
    }

    public BPlayer getBPlayer(final String name) {
        return getBPlayer(plugin.getServer().getPlayer(name));
    }

    public boolean containsBPlayer(final UUID uuid) {
        return players.containsKey(uuid);
    }

    public Collection<BPlayer> getBPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

}