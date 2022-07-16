package net.brutewars.sandbox.menu.items;

import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class CyclingMenuItem extends MenuItem {
    private final BWorldPlugin plugin;

    private final List<MenuItem> items = new ArrayList<>();

    // Modify which is the starting item based on the player
    @Setter private Function<BPlayer, Integer> startingIndex;

    public CyclingMenuItem(BWorldPlugin plugin, MenuItem item) {
        super(null, null);
        this.plugin = plugin;
        add(item);
    }

    public void add(MenuItem item) {
        int size = items.size();

        items.add(new MenuItem(setIndexKey(item.getItem(null)), (event, bPlayer) -> {
            event.getInventory().setItem(event.getSlot(), items.get((size == items.size() - 1) ? 0 : size + 1).getItem(bPlayer));
            item.getAction().accept(event, bPlayer);
        }));
    }

    @Override
    public ItemStack getItem(BPlayer bPlayer) {
        return items.get((startingIndex == null) ? 0 : startingIndex.apply(bPlayer)).getItem(bPlayer);
    }

    @Override
    public BiConsumer<InventoryClickEvent, BPlayer> getAction() {
        return (event, bPlayer) -> {
            ItemStack item = event.getCurrentItem();
            if (item == null || !item.hasItemMeta())
                return;

            items.get(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER))
                    .getAction().accept(event, bPlayer);
        };
    }

    private ItemStack setIndexKey(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "index");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, items.size());
        item.setItemMeta(itemMeta);
        return item;
    }



}