package net.brutewars.sandbox.config.parser;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.utils.Logging;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public final class WorldSizeParser extends SectionParser {
    private final Map<String, Integer> worldSizes = new HashMap<>();

    public WorldSizeParser(BWorldPlugin plugin) {
        super(plugin, "world.sizes");
    }

    @Override
    public void parse() {
        ConfigurationSection sizeSection = plugin.getConfig().getConfigurationSection(path);
        if (sizeSection == null)
            return;

        worldSizes.clear();
        sizeSection.getKeys(false).forEach(s -> this.worldSizes.put(s, sizeSection.getInt(s)));

        if (!worldSizes.containsKey("default")) {
            Logging.severe("default world size could not be found. Default will be set to 1000 blocks");
            worldSizes.put("default", 1000);
        }

    }

    public Map<String, Integer> getWorldSizes(boolean reload) {
        if (reload)
            parse();

        return worldSizes;
    }

}