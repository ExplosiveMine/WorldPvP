package net.brutewars.sandbox.menu.items;

import lombok.Setter;
import net.brutewars.sandbox.player.BPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class CyclingItem extends MenuItem {
    private final List<MenuItem> items;

    // Modify which is the starting item based on the player
    @Setter private Function<BPlayer, Integer> startingIndex;

    public CyclingItem(MenuItem item) {
        super(null, null);
        items = new ArrayList<>();
        add(item);
    }

    public void add(MenuItem item) {
        int size = items.size();
        items.add(new MenuItem(setNBTTag(item.getItem()), (event, bPlayer) -> {
            event.getInventory().setItem(event.getSlot(), items.get((size == items.size() - 1) ? 0 : size + 1).getItem());
            item.getAction().accept(event, bPlayer);
        }));
    }

    @Override
    public ItemStack getItem(BPlayer bPlayer) {
        return items.get((startingIndex == null) ? 0 : startingIndex.apply(bPlayer)).getItem(bPlayer);
    }

    @Override
    public BiConsumer<InventoryClickEvent, BPlayer> getAction() {
        return (event, bPlayer) -> items.get(CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag().getInt("index")).getAction().accept(event, bPlayer);
    }

    private ItemStack setNBTTag(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbtTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        nbtTag.setInt("index", items.size());
        nmsItem.setTag(nbtTag);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

}