package net.brutewars.sandbox.menu.menus.pagination;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.BiConsumer;

public final class MenuPage extends Menu {

    public MenuPage(BWorldPlugin plugin, MenuIdentifier identifier, String title, int size) {
        super(plugin, identifier, title, size);
    }

    @Override
    public void placeItems() {
        //noop
    }

    @Override
    public void onClose(InventoryCloseEvent event, BPlayer bPlayer) {
        plugin.getMenuManager().getMenu(identifier).onClose(event, bPlayer);
    }

    public void setNextArrow(int slot, BiConsumer<InventoryClickEvent, BPlayer> biConsumer) {
        if (biConsumer == null)
            return;

        setItem(slot, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .setDisplayName("&6Next")
                .onClick(biConsumer));
    }

    public void setPreviousArrow(int slot, BiConsumer<InventoryClickEvent, BPlayer> biConsumer) {
        if (biConsumer == null)
            return;

        setItem(slot, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .setDisplayName("&6Previous")
                .onClick(biConsumer));
    }

}