package net.brutewars.sandbox.menu;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class MenuListener implements Listener {
    private final BWorldPlugin plugin;

    public MenuListener(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;

        // prevent player from moving items therefore by default the event is cancelled when passed to menu items
        event.setCancelled(true);

        // empty slot was clicked. even if this slot is mapped to an item, the item wasn't added on purpose, and therefore we return
        // this is done after cancelling the event to prevent any items on the cursors from being put in the empty slot.
        if (event.getCurrentItem() == null || Material.AIR.equals(event.getCurrentItem().getType()))
            return;

        BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(event.getWhoClicked().getUniqueId());

        Menu menu = (Menu) event.getInventory().getHolder();

        MenuItem item = menu.getItemAt(event.getSlot());
        if (item != null && item.getAction() != null)
            item.getAction().accept(event, bPlayer);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;

        BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(event.getPlayer().getUniqueId());

        Menu menu = (Menu) event.getInventory().getHolder();
        menu.onClose(event, bPlayer);
    }

}