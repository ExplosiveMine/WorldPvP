package net.brutewars.sandbox.world.bonus;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class BonusChest {
    private final BWorldPlugin plugin;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final List<BonusItem> bonusItems;
    private final int min, max;

    public BonusChest(final BWorldPlugin plugin) {
        this.plugin = plugin;
        bonusItems = new ArrayList<>();
        this.min = plugin.getConfig().getInt("bonus chest.minimum items", 5);
        this.max = plugin.getConfig().getInt("bonus chest.maximum items", 5);
        init();
    }

    private void init() {
        final ConfigurationSection bonus_chest = plugin.getConfig().getConfigurationSection("bonus chest.items");
        bonus_chest.getKeys(false).forEach(s -> {
            final ConfigurationSection key = bonus_chest.getConfigurationSection(s);
            bonusItems.add(new BonusItem(
                    key.getString( "material", "AIR"),
                    key.getInt("min", 1),
                    key.getInt("max", 1),
                    key.getInt("sharp", 0)));
        });
    }

    public void spawn(final Location location) {
        location.getBlock().setType(Material.CHEST);
        final Chest chest = (Chest) location.getBlock().getState();
        final Inventory inv = chest.getInventory();

        final Iterator<ItemStack> itr = Arrays.stream(getItems()).iterator();
        while (itr.hasNext()) {
            int slot;
            do {
                slot = random.nextInt(0, inv.getSize());
            } while (inv.getItem(slot) != null && !Material.AIR.equals(inv.getItem(slot).getType()));
            inv.setItem(slot, itr.next());
        }
        chest.update();
    }

    private ItemStack[] getItems() {
        final int num = random.nextInt(min, max + 1);

        final ItemStack[] items = new ItemStack[num];
        for (int i = 1; i <= num; i++)
            items[i-1] = bonusItems.get(random.nextInt(0, bonusItems.size())).toItem();

        return items;
    }

}