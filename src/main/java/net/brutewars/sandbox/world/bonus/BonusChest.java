package net.brutewars.sandbox.world.bonus;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.utils.Logging;
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

    public BonusChest(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawn(Location location) {
        location.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) location.getBlock().getState();
        Inventory inv = chest.getSnapshotInventory();

        Iterator<ItemStack> itr = Arrays.stream(plugin.getConfigSettings().getBonusChestParser().getItems()).iterator();
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