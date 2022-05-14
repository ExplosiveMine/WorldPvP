package net.brutewars.sandbox.config.parser;

import net.brutewars.sandbox.world.bonus.BonusItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class BonusChestParser implements SectionParser {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final List<BonusItem> bonusItems;
    public int min, max;

    public BonusChestParser(ConfigurationSection configurationSection) {
        bonusItems = new ArrayList<>();
        parse(configurationSection);
    }

    @Override
    public void parse(final ConfigurationSection bonusChestSection) {
        this.min = bonusChestSection.getInt("minimum items", 5);
        this.max = bonusChestSection.getInt("maximum items", 5);

        bonusChestSection.getConfigurationSection("items").getKeys(false).forEach(s -> {
            final ConfigurationSection item = bonusChestSection.getConfigurationSection("items." + s);
            bonusItems.add(new BonusItem(
                    item.getString( "material", "AIR"),
                    item.getInt("min", 1),
                    item.getInt("max", 1),
                    item.getInt("sharp", 0)));
        });
    }

    public ItemStack[] getItems() {
        final int num = random.nextInt(min, max + 1);

        final ItemStack[] items = new ItemStack[num];
        for (int i = 1; i <= num; i++)
            items[i-1] = bonusItems.get(random.nextInt(0, bonusItems.size())).toItem();

        return items;
    }

}