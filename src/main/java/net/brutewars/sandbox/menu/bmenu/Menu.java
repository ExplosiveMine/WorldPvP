package net.brutewars.sandbox.menu.bmenu;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.MenuAction;
import net.brutewars.sandbox.menu.items.BaseMenuItem;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {
    protected final BWorldPlugin plugin;

    // identifier of the inventory
    @Getter protected final String identifier;

    // identifier of the parent menu
    @Getter protected final String parentMenuId;

    // inventory title
    protected final String title;

    // inventory type
    @Getter protected final InventoryType type;

    // inventory size
    @Getter protected final int size;

    // what to do when the inventory closes
    @Setter protected MenuAction<InventoryCloseEvent, Boolean> closeAction;

    // the default items in the inventory
    protected final Map<Integer, BaseMenuItem> defaultItems = new HashMap<>();

    public Menu(BWorldPlugin plugin, String identifier, InventoryType type, String title, String parentMenuId) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.type = type;
        this.title = StringUtils.colour(title);
        this.size = type.getDefaultSize();
        this.parentMenuId = parentMenuId;
        init();
    }

    public Menu(BWorldPlugin plugin, String identifier, String title, int size, String parentMenuId) {
        this.plugin = plugin;
        this.identifier =identifier;
        this.type = InventoryType.CHEST;
        this.title = StringUtils.colour(title);
        this.size = size;
        this.parentMenuId = parentMenuId;
        init();
    }

    public abstract void init();

    public void open(final BPlayer bPlayer) {
        if (bPlayer.isSleeping()) return;

        bPlayer.openInventory(build(bPlayer, title));
    }

    protected Inventory build(final BPlayer bPlayer, final String title) {
        Inventory inventory;

        if (this.type.equals(InventoryType.CHEST))
            inventory = Bukkit.createInventory(this, size, title);
        else
            inventory = Bukkit.createInventory(this, type, title);

        defaultItems.forEach((key, value) -> inventory.setItem(key, value.getItem(bPlayer)));

        return inventory;
    }

    /**
     * @return A default inventory with all the items without applying their respective functions
     */
    @Override
    public Inventory getInventory() {
        final Inventory inventory;

        if (this.type.equals(InventoryType.CHEST))
            inventory = Bukkit.createInventory(this, size, title);
        else
            inventory = Bukkit.createInventory(this, type, title);

        defaultItems.forEach((key, value) -> inventory.setItem(key, value.getItem()));

        return inventory;
    }

    public void close(final BPlayer bPlayer, boolean openParentMenu) {
        bPlayer.runIfOnline(HumanEntity::closeInventory);

        if (openParentMenu)
            plugin.getMenuManager().openParentMenu(bPlayer, identifier);
    }

    public void onClose(InventoryCloseEvent event, BPlayer bPlayer) {
        boolean reopenInventory = false;

        if (closeAction != null)
            reopenInventory = closeAction.apply(event);

        if (reopenInventory)
            bPlayer.openInventory(event.getInventory());
    }

    public void setItem(final int slot, final BaseMenuItem menuItem) {
        defaultItems.put(slot, menuItem);
    }

    public BaseMenuItem getItemAt(final int slot) {
        return defaultItems.get(slot);
    }

}