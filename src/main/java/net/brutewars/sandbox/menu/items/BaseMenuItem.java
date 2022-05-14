package net.brutewars.sandbox.menu.items;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;


public abstract class BaseMenuItem {
    @Getter private final ItemStack item;

    // action to do on inventory click
    @Getter private final BiConsumer<InventoryClickEvent, BPlayer> action;

    // modify the item based on the player
    @Getter @Setter private BiFunction<ItemStack, BPlayer, ItemStack> function;

    public BaseMenuItem(final ItemStack item, final BiConsumer<InventoryClickEvent, BPlayer> action) {
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem(final BPlayer bPlayer) {
        return function == null ? item : function.apply(item, bPlayer);
    }

}