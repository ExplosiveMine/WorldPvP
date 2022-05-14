package net.brutewars.sandbox.menu;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.items.BaseMenuItem;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class MenuListener implements Listener {
    private final BWorldPlugin plugin;

    public MenuListener(final BWorldPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;

        // click was outside the inventory
        if (event.getClickedInventory() == null) return;

        // prevent player from moving items therefore by default the event is cancelled when passed to menu items
        event.setCancelled(true);

        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(event.getWhoClicked().getUniqueId());

        final Menu menu = (Menu) event.getInventory().getHolder();

        final BaseMenuItem item = menu.getItemAt(event.getSlot());
        if (item != null && item.getAction() != null)
            item.getAction().accept(event, bPlayer);
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;

        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(event.getPlayer().getUniqueId());

        final Menu menu = (Menu) event.getInventory().getHolder();
        menu.onClose(event, bPlayer);
    }

}