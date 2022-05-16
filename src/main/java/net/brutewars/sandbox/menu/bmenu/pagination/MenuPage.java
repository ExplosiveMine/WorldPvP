package net.brutewars.sandbox.menu.bmenu.pagination;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.items.ItemFactory;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;

public final class MenuPage extends Menu {
    @Getter private final int pageId;

    public MenuPage(BWorldPlugin plugin, String parentMenuId, int pageId, String title, int size) {
        super(plugin, parentMenuId + ":" + pageId, title, size, parentMenuId);
        this.pageId = pageId;
    }

    @Override
    public void placeItems() {
        //noop
    }

    public void setNextArrow(int slot, BiConsumer<InventoryClickEvent, BPlayer> biConsumer) {
        if (biConsumer == null) return;
        setItem(slot, ItemFactory.createMenuArrow("&6Next", biConsumer));
    }

    public void setPreviousArrow(int slot, BiConsumer<InventoryClickEvent, BPlayer> biConsumer) {
        if (biConsumer == null) return;
        setItem(slot, ItemFactory.createMenuArrow("&6Previous", biConsumer));
    }

}