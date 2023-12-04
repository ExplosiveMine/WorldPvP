package net.brutewars.sandbox.menu.items;

import lombok.Getter;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;


public class MenuItem {
    private final ItemStack item;

    @Getter private final BiConsumer<InventoryClickEvent, BPlayer> onClickAction;

    // modify the item based on the player
    @Getter private final BiFunction<ItemStack, BPlayer, ItemStack> playerFunction;

    public MenuItem(ItemStack item, BiConsumer<InventoryClickEvent, BPlayer> onClickAction) {
        this(item, onClickAction, null);
    }

    public MenuItem(ItemStack item, BiConsumer<InventoryClickEvent, BPlayer> action, BiFunction<ItemStack, BPlayer, ItemStack> playerFunction) {
        this.item = item;
        this.onClickAction = action;
        this.playerFunction = playerFunction;
    }

    public ItemStack getItem(BPlayer bPlayer) {
        return (playerFunction == null || bPlayer == null) ? item : playerFunction.apply(item.clone(), bPlayer);
    }

}