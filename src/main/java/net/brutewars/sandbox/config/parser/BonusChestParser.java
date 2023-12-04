package net.brutewars.sandbox.config.parser;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.bonus.BonusItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class BonusChestParser extends SectionParser {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final List<BonusItem> bonusItems = new ArrayList<>();
    private int min, max;

    public BonusChestParser(BWorldPlugin plugin) {
        super(plugin, "bonus-chest.yml");
    }

    @Override
    public void parse() {
        min = getSection().getInt("minimum items", 5);
        max = getSection().getInt("maximum items", 5);

        getSection().getConfigurationSection("items").getKeys(false).forEach(s -> {
            ConfigurationSection item = getSection().getConfigurationSection("items." + s);
            bonusItems.add(new BonusItem(
                    item.getString( "material", "AIR"),
                    item.getInt("min", 1),
                    item.getInt("max", 1)));
        });
    }

    public ItemStack[] getItems() {
        int num = random.nextInt(min, max + 1);

        ItemStack[] items = new ItemStack[num];
        for (int i = 1; i <= num; i++)
            items[i-1] = bonusItems.get(random.nextInt(0, bonusItems.size())).toItem();

        return items;
    }

}