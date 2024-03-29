package net.brutewars.sandbox.player;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BPlayer {
    private final BWorldPlugin plugin;

    @Getter private final UUID uuid;

    @Getter @Setter private BWorld bWorld;
    @Getter private final List<UUID> additionalBWorlds;

    // HUD items
    @Getter @Setter private int hudSlot = 9;
    @Getter @Setter private boolean hudToggled = true;

    public BPlayer(BWorldPlugin plugin, UUID uuid, BWorld bWorld) {
        this.plugin = plugin;
        this.bWorld = bWorld;
        this.uuid = uuid;
        this.additionalBWorlds = new ArrayList<>();
    }

    public void addBWorld(BWorld bWorld) {
        additionalBWorlds.add(bWorld.getUuid());
    }

    public void removeBWorld(BWorld bWorld) {
        additionalBWorlds.remove(bWorld.getUuid());
    }

    public boolean isInBWorld(@Nullable BWorld bWorld, boolean includeOwnBWorld) {
        if (bWorld == null)
            return false;

        return additionalBWorlds.contains(bWorld.getUuid()) || (includeOwnBWorld && bWorld.equals(this.bWorld));
    }

    public OfflinePlayer toOfflinePlayer() {
        return plugin.getServer().getOfflinePlayer(uuid);
    }

    private Player toPlayer() {
        return plugin.getServer().getPlayer(uuid);
    }

    public void runIfOnline(Consumer<Player> consumer) {
        if (!isOnline())
            return;

        consumer.accept(toPlayer());
    }

    public <T> T getIfOnline(Function<Player, T> function) {
        if (!isOnline())
            return null;

        return function.apply(toPlayer());
    }

    public boolean isOnline() {
        return toOfflinePlayer().isOnline();
    }

    public String getName() {
        return toOfflinePlayer().getName();
    }

    public boolean hasPermission(String permission) {
        return plugin.getVault().hasPermission(toOfflinePlayer(), permission);
    }

    public void playSound(Sound sound, float v, float v1) {
        if (!isOnline())
            return;

        Player player = toPlayer();
        player.playSound(player.getLocation(), sound, v, v1);
    }

    public boolean isSleeping() {
        return isOnline() && toPlayer().isSleeping();
    }

    public void openInventory(Inventory inventory) {
        if (!isOnline()) return;

        toPlayer().openInventory(inventory);
    }

    public void setGameMode(GameMode gameMode) {
        runIfOnline(player -> player.setGameMode(gameMode));
    }

}