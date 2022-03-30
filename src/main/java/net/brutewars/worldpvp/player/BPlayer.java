package net.brutewars.worldpvp.player;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.rank.Rank;
import net.brutewars.worldpvp.world.BWorld;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public final class BPlayer {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;
    @Getter @Setter private BWorld bWorld;
    @Getter private Rank rank;
    @Getter @Setter private PlayerChat playerChat;

    public BPlayer(final BWorldPlugin plugin, final UUID uuid, BWorld bWorld, Rank rank) {
        this.plugin = plugin;
        this.rank = rank;
        this.bWorld = bWorld;
        this.uuid = uuid;
        this.playerChat = PlayerChat.GLOBAL;
    }

    private OfflinePlayer toOfflinePlayer() {
        return plugin.getServer().getOfflinePlayer(uuid);
    }

    private Player toPlayer() {
        return plugin.getServer().getPlayer(uuid);
    }

    public void runIfOnline(Consumer<Player> consumer) {
        if (!isOnline()) return;
        consumer.accept(toPlayer());
    }

    public boolean isOnline() {
        return toOfflinePlayer().isOnline();
    }

    public String getName() {
        return toOfflinePlayer().getName();
    }

    public boolean hasPermission(final String permission) {
        return plugin.getVault().hasPermission(toOfflinePlayer(), permission);
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void sendToWorld() {
        teleport(plugin.getServer().getWorld(bWorld.getWorldName()).getSpawnLocation());
    }

    public void sendToSpawn() {
        runIfOnline(player ->
                //todo
                //player.performCommand("spawn"));
                teleport(new Location(plugin.getServer().getWorld("world"), 0, 100, 0)));
    }

    public void teleport(final Location location) {
        if (!isOnline() || bWorld == null)
            return;

        toPlayer().teleport(location);
    }

}