package net.brutewars.sandbox.config.parser;

import lombok.Getter;
import net.brutewars.sandbox.menu.bmenu.animation.Frame;
import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.menu.items.ItemFactory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public final class AnimatedMenuParser implements SectionParser {
    @Getter private final Map<String, MenuItem> items = new HashMap<>();
    @Getter private final List<Frame> frames = new ArrayList<>();

    @Getter private long animationSpeed;

    public AnimatedMenuParser(ConfigurationSection configurationSection) {
        parse(configurationSection);
    }

    @Override
    public void parse(ConfigurationSection configurationSection) {
        animationSpeed = configurationSection.getLong("animationSpeed", 7);

        List<String> patterns = configurationSection.getStringList("frames");
        for (String s : patterns)
            frames.add(new Frame(s.split(":")));

        ConfigurationSection materials = configurationSection.getConfigurationSection("materials");
        Set<String> keys = materials.getKeys(false);

        for (String key : keys) {
            Material material = Material.valueOf(materials.getString(key + ".type", "AIR"));
            int value = materials.getInt(key + ".value", 0);
            String displayName = materials.getString(key + ".displayName", "");
            boolean enchanted = materials.getBoolean(key + ".enchanted", false);

            MenuItem item = ItemFactory.createItem(material, value, displayName, null);
            item.setGlowing(enchanted);

            items.put(key, item);
        }
    }

}