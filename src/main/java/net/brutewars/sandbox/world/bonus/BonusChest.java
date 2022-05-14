package net.brutewars.sandbox.world.bonus;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public final class BonusChest {
    private final BWorldPlugin plugin;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public BonusChest(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawn(final Location location) {
        location.getBlock().setType(Material.CHEST);
        final Chest chest = (Chest) location.getBlock().getState();
        final Inventory inv = chest.getInventory();

        final Iterator<ItemStack> itr = Arrays.stream(plugin.getConfigSettings().bonusChestParser.getItems()).iterator();
        while (itr.hasNext()) {
            int slot;
            do {
                slot = random.nextInt(0, inv.getSize());
            } while (inv.getItem(slot) != null && !Material.AIR.equals(inv.getItem(slot).getType()));
            inv.setItem(slot, itr.next());
        }
        chest.update();
    }

}