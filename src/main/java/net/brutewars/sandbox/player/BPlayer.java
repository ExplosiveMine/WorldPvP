package net.brutewars.sandbox.player;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public <T> T getIfOnline(Function<Player, T> function) {
        if (!isOnline()) return null;
        return function.apply(toPlayer());
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

    public void playSound(Sound sound, float v, float v1) {
        if (!isOnline())
            return;

        final Player player = toPlayer();
        player.playSound(player.getLocation(), sound, v, v1);
    }

    public boolean isSleeping() {
        return isOnline() && toPlayer().isSleeping();
    }

    public void openInventory(final Inventory inventory) {
        if (!isOnline()) return;

        toPlayer().openInventory(inventory);
    }

}