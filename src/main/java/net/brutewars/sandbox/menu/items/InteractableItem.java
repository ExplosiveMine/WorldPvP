package net.brutewars.sandbox.menu.items;

import lombok.Getter;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class InteractableItem {
    private final ItemStack item;

    // action to do on inventory click
    @Getter
    private final BiConsumer<PlayerInteractEvent, BPlayer> action;

    // modify the item based on the player
    @Getter private final BiFunction<ItemStack, BPlayer, ItemStack> function;

    public InteractableItem(ItemStack item, BiConsumer<PlayerInteractEvent, BPlayer> action) {
        this(item, action, null);
    }

    public InteractableItem(ItemStack item, BiConsumer<PlayerInteractEvent, BPlayer> action, BiFunction<ItemStack, BPlayer, ItemStack> function) {
        this.item = item;
        this.action = action;
        this.function = function;
    }

    public ItemStack getItem(BPlayer bPlayer) {
        return (function == null || bPlayer == null) ? item : function.apply(item.clone(), bPlayer);
    }

}
