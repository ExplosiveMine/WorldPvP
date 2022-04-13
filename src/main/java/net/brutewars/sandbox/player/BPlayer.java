package net.brutewars.sandbox.player;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public final class BPlayer {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;

    @Getter @Setter private BWorld bWorld;
    @Getter private final Set<UUID> additionalWorlds;

    public BPlayer(final BWorldPlugin plugin, final UUID uuid, BWorld bWorld) {
        this.plugin = plugin;
        this.bWorld = bWorld;
        this.uuid = uuid;
        this.additionalWorlds = new HashSet<>();
    }

    public void addBWorld(final BWorld bWorld) {
        additionalWorlds.add(bWorld.getUuid());
    }

    public void removeBWorld(final BWorld bWorld) {
        additionalWorlds.remove(bWorld.getUuid());
    }

    public boolean isInBWorld(final BWorld bWorld, boolean includeOwnBWorld) {
        if (bWorld == null) return false;
        return additionalWorlds.contains(bWorld.getUuid()) || (includeOwnBWorld && bWorld.equals(this.bWorld));
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

    public void teleport(final Location location) {
        if (!isOnline())
            return;

        toPlayer().teleport(location);
    }

}