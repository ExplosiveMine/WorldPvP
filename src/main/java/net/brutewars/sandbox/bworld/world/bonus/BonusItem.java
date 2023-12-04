package net.brutewars.sandbox.bworld.world.bonus;

import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class BonusItem {
    private final ItemBuilder itemBuilder;
    private final int min, max;

    public BonusItem(String mat, int min, int max) {
        itemBuilder = new ItemBuilder(Material.valueOf(mat));
        this.min = min;
        this.max = max;
    }

    public ItemStack toItem() {
        return itemBuilder.setAmount(ThreadLocalRandom.current().nextInt(min, max + 1)).toItem();
    }

}