package net.brutewars.sandbox.menu.bmenu;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.MenuAction;
import net.brutewars.sandbox.menu.items.MenuItem;
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
import java.util.function.Function;

public abstract class Menu implements InventoryHolder {
    protected final BWorldPlugin plugin;

    @Getter protected final String identifier;
    @Getter protected final String parentMenuId;

    protected final String title;
    protected Function<BPlayer, String> titleProvider;

    @Getter protected final InventoryType type;

    @Getter protected final int size;

    @Setter protected MenuAction<InventoryCloseEvent, Boolean> closeAction;

    protected final Map<Integer, MenuItem> defaultItems = new HashMap<>();

    public Menu(BWorldPlugin plugin, String identifier, String title, InventoryType type, String parentMenuId) {
        this(plugin, identifier, title, type, type.getDefaultSize(), parentMenuId);
    }

    public Menu(BWorldPlugin plugin, String identifier, String title, int size, String parentMenuId) {
        this(plugin, identifier, title, InventoryType.CHEST, size, parentMenuId);
    }

    private Menu(BWorldPlugin plugin, String identifier, String title, InventoryType type, int size, String parentMenuId) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.type = type;
        this.size = size;
        this.title = StringUtils.colour(title);
        this.parentMenuId = parentMenuId;
    }

    /**
     * @return An inventory with default items and default title.
     */
    @Override
    public Inventory getInventory() {
        return build(null);
    }

    public abstract void placeItems();

    public void open(BPlayer bPlayer) {
        if (bPlayer.isSleeping())
            return;

        bPlayer.openInventory(build(bPlayer));
    }

    protected Inventory build(BPlayer bPlayer) {
        if (defaultItems.isEmpty())
            placeItems();

        Inventory inventory = createInv(bPlayer);
        defaultItems.forEach((key, value) -> inventory.setItem(key, value.getItem(bPlayer)));

        return inventory;
    }

    protected Inventory createInv(BPlayer bPlayer) {
        String title = (bPlayer == null || titleProvider == null) ? getTitle() : titleProvider.apply(bPlayer);
        return InventoryType.CHEST.equals(type) ? Bukkit.createInventory(this, size, title) : Bukkit.createInventory(this, type, title);
    }

    protected String getTitle() {
        return StringUtils.colour(title);
    }

    public void close(BPlayer bPlayer, boolean openParentMenu) {
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

    public void setItem(int slot, MenuItem menuItem) {
        defaultItems.put(slot, menuItem);
    }

    public MenuItem getItemAt(int slot) {
        return defaultItems.get(slot);
    }

}