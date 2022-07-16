package net.brutewars.sandbox.menu.items.builders;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.items.CyclingMenuItem;
import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class CyclingItemBuilder extends BaseItemBuilder<CyclingItemBuilder> {
    private final BWorldPlugin plugin;

    private final List<BaseItemBuilder<?>> builders = new ArrayList<>();

    private Function<BPlayer, Integer> startingIndex;

    public CyclingItemBuilder(BWorldPlugin plugin) {
        super(new ItemStack(Material.AIR));
        this.plugin = plugin;
    }

    public CyclingItemBuilder add(BaseItemBuilder<?> itemBuilders) {
        builders.add(itemBuilders);
        return this;
    }

    public CyclingItemBuilder setStartingIndex(Function<BPlayer, Integer> startingIndex) {
        this.startingIndex = startingIndex;
        return this;
    }

    @Override
    public MenuItem toMenuItem() {
        CyclingMenuItem menuItem = new CyclingMenuItem(plugin, builders.get(0).toMenuItem());
        for (int i = 1; i < builders.size(); i++)
            menuItem.add(builders.get(i).toMenuItem());
        menuItem.setStartingIndex(startingIndex);

        return menuItem;
    }
}