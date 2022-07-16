package net.brutewars.sandbox.config.parser;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class SectionParser {
    protected final BWorldPlugin plugin;
    protected final String path;

    private ConfigurationSection section;

    public SectionParser(BWorldPlugin plugin, String path) {
        this.plugin = plugin;
        this.path = path;
    }

    public abstract void parse();

    public ConfigurationSection getSection() {
        if (section != null)
            return section;

        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists())
            plugin.saveResource(path, false);
        return YamlConfiguration.loadConfiguration(file);
    }

}