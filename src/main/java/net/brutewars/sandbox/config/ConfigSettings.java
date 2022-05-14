package net.brutewars.sandbox.config;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.AnimatedMenuParser;
import net.brutewars.sandbox.config.parser.BonusChestParser;
import net.brutewars.sandbox.config.parser.CommandCooldownParser;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.world.WorldSize;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;


public final class ConfigSettings {
    private final BWorldPlugin plugin;

    public CommandCooldownParser commandCooldownParser;
    public BonusChestParser bonusChestParser;
    public AnimatedMenuParser creatingAnimationParser;

    public long invitingTime;
    public long resettingTime;
    public long unloadingTime;

    public String worldName;

    public boolean debug;

    public ConfigSettings(final BWorldPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        plugin.saveDefaultConfig();
        Lang.reload(plugin);
        WorldSize.reload(plugin);

        FileConfiguration config;

        /*
         * bonus-chest.yml
         */
        config = getConfig("bonus-chest.yml");
        bonusChestParser = new BonusChestParser(config.getConfigurationSection("bonus chest"));

        /*
         * creating-animation.yml
         */
        config = getConfig("menu" + File.separator + "creating-animation.yml");
        creatingAnimationParser = new AnimatedMenuParser(config.getConfigurationSection("creating animation"));

        /*
         * config.yml
         */
        config = plugin.getConfig();

        commandCooldownParser = new CommandCooldownParser(config.getConfigurationSection("commands.cooldown"));

        invitingTime = config.getLong("invites.expire time");
        resettingTime = config.getLong("world.resetting time");
        unloadingTime = config.getLong("world.unloading time");

        worldName = config.getString("spawnWorld");

        debug = config.getBoolean("debug");
    }

    private YamlConfiguration getConfig(final String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists())
            plugin.saveResource(resourcePath, false);
        return YamlConfiguration.loadConfiguration(file);
    }

}