package net.brutewars.sandbox.world.bonus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class BonusItem {
    private final ItemStack item;
    private final int min, max;

    public BonusItem(final String mat, final int min, final int max, final int sharp) {
        this.item = new ItemStack(Material.valueOf(mat), 1, (short) sharp);
        this.min = min;
        this.max = max;
    }

    public ItemStack toItem() {
        item.setAmount(ThreadLocalRandom.current().nextInt(min, max + 1));
        return item;
    }

}