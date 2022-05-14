package net.brutewars.sandbox.menu.items;

import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public final class MenuItem extends BaseMenuItem {
    public MenuItem(final ItemStack item, final BiConsumer<InventoryClickEvent, BPlayer> action) {
        super(item, action);
    }

    public MenuItem(final ItemStack item) {
        super(item, null);
    }
}