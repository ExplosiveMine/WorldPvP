package net.brutewars.sandbox.config.parser;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.menus.animation.Frame;
import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import net.brutewars.sandbox.menu.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public final class AnimatedMenuParser extends SectionParser {
    @Getter private final Map<String, MenuItem> items = new HashMap<>();
    @Getter private final List<Frame> frames = new ArrayList<>();

    @Getter private long animationSpeed;

    public AnimatedMenuParser(BWorldPlugin plugin, String path) {
        super(plugin, path);
    }

    @Override
    public void parse() {
        animationSpeed = getSection().getLong("animation speed", 7);

        List<String> patterns = getSection().getStringList("frames");
        for (String s : patterns)
            frames.add(new Frame(s.split(":")));

        ConfigurationSection materials = getSection().getConfigurationSection("materials");
        Set<String> keys = materials.getKeys(false);

        for (String key : keys) {
            Material material = Material.valueOf(materials.getString(key + ".type", "AIR"));
            String displayName = materials.getString(key + ".displayName", "");
            boolean enchanted = materials.getBoolean(key + ".enchanted", false);

            items.put(key, new ItemBuilder(material)
                    .setDisplayName(displayName)
                    .setGlowing(enchanted)
                    .toMenuItem());
        }
    }
}