package net.brutewars.sandbox.menu.items;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;


public class MenuItem {
    @Getter private final ItemStack item;

    // action to do on inventory click
    @Getter private final BiConsumer<InventoryClickEvent, BPlayer> action;

    // modify the item based on the player
    @Getter @Setter private BiFunction<ItemStack, BPlayer, ItemStack> function;

    public MenuItem(ItemStack item, BiConsumer<InventoryClickEvent, BPlayer> action) {
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem(BPlayer bPlayer) {
        return function == null ? item : function.apply(item, bPlayer);
    }

    public void setGlowing(boolean glowing) {
        if (!glowing) return;

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
    }

}