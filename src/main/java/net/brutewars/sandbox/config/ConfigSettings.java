package net.brutewars.sandbox.config;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.*;
import net.brutewars.sandbox.world.WorldSize;

import java.io.File;

public final class ConfigSettings {
    private final BWorldPlugin plugin;

    @Getter private CommandCooldownParser commandCooldownParser;
    @Getter private BonusChestParser bonusChestParser;
    @Getter private AnimatedMenuParser creatingAnimationParser;
    @Getter private ConfigParser configParser;

    public ConfigSettings(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        plugin.saveDefaultConfig();
        Lang.reload(plugin);
        WorldSize.reload(plugin);

        bonusChestParser = new BonusChestParser(plugin);
        creatingAnimationParser = new AnimatedMenuParser(plugin, "menu" + File.separator + "creating-animation.yml");
        commandCooldownParser = new CommandCooldownParser(plugin);
        configParser = new ConfigParser(plugin);

        SectionParser[] parsers = new SectionParser[] {
                bonusChestParser,
                creatingAnimationParser,
                commandCooldownParser,
                configParser
        };

        for (SectionParser parser : parsers)
            parser.parse();
    }

}