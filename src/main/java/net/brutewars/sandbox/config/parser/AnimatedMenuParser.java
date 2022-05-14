package net.brutewars.sandbox.config.parser;

import lombok.Getter;
import net.brutewars.sandbox.menu.bmenu.animation.Frame;
import net.brutewars.sandbox.menu.items.BaseMenuItem;
import net.brutewars.sandbox.menu.items.ItemFactory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public final class AnimatedMenuParser implements SectionParser {
    @Getter private final Map<String, BaseMenuItem> items = new HashMap<>();
    @Getter private final List<Frame> frames = new ArrayList<>();

    @Getter private long animationSpeed;

    public AnimatedMenuParser(final ConfigurationSection configurationSection) {
        parse(configurationSection);
    }

    @Override
    public void parse(ConfigurationSection configurationSection) {
        animationSpeed = configurationSection.getLong("animationSpeed", 7);

        final List<String> patterns = configurationSection.getStringList("frames");
        for (String s : patterns)
            frames.add(new Frame(s.split(":")));

        final ConfigurationSection materials = configurationSection.getConfigurationSection("materials");
        final Set<String> keys = materials.getKeys(false);

        for (String key : keys) {
            final Material material = Material.valueOf(materials.getString(key + ".type", "AIR"));
            final int value = materials.getInt(key + ".value", 0);
            final String displayName = materials.getString(key + ".displayName", "");
            final boolean enchanted = materials.getBoolean(key + ".enchanted", false);

            final BaseMenuItem item = ItemFactory.createItem(material, value, displayName, null);
            item.setGlowing(enchanted);

            items.put(key, item);
        }
    }

}