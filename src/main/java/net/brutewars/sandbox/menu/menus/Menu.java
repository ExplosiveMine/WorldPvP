package net.brutewars.sandbox.menu.menus;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.MenuAction;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.items.builders.BaseItemBuilder;
import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class Menu implements InventoryHolder {
    protected final BWorldPlugin plugin;

    @Getter protected final MenuIdentifier identifier;

    protected final String title;
    protected Function<BPlayer, String> titleProvider;

    @Getter protected final InventoryType type;

    @Getter protected final int size;

    @Setter protected MenuAction<InventoryCloseEvent, Boolean> closeAction;

    @Getter @Setter protected BaseItemBuilder<?> filler;

    protected final Map<Integer, MenuItem> defaultItems = new HashMap<>();
    protected boolean setup = false;

    public Menu(BWorldPlugin plugin, MenuIdentifier identifier, String title, InventoryType type) {
        this(plugin, identifier, title, type, type.getDefaultSize());
    }

    public Menu(BWorldPlugin plugin, MenuIdentifier identifier, String title, int size) {
        this(plugin, identifier, title, InventoryType.CHEST, size);
    }

    private Menu(BWorldPlugin plugin, MenuIdentifier identifier, String title, InventoryType type, int size) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.type = type;
        this.size = size;
        this.title = title;
    }

    /**
     * @return An inventory with default items and default title.
     */
    @Override @NotNull
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
        if (!setup) {
            placeItems();
            setup = true;
        }

        Inventory inventory = createInv(bPlayer);
        populate(inventory, bPlayer);
        defaultItems.forEach((key, value) -> inventory.setItem(key, value.getItem(bPlayer)));
        return inventory;
    }

    public void populate(Inventory inventory, BPlayer bPlayer) {
        if (getFiller() != null) {
            for (int i = 0; i < size; i++)
                inventory.setItem(i, getFiller().toItem());
        }

        defaultItems.forEach((key, value) -> inventory.setItem(key, value.getItem(bPlayer)));
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

    public void setItem(int slot, BaseItemBuilder<?> builder) {
        defaultItems.put(slot, builder.toMenuItem());
    }

    public void clickItemAt(InventoryClickEvent event) {
        BPlayer mPlayer = plugin.getBPlayerManager().get(event.getWhoClicked().getUniqueId());
        MenuItem item = defaultItems.get(event.getSlot());
        if (item != null && item.getAction() != null)
            item.getAction().accept(event, mPlayer);
    }

    public void update(BPlayer bPlayer) {
        bPlayer.runIfOnline(player -> populate(player.getOpenInventory().getTopInventory(), bPlayer));
    }
}