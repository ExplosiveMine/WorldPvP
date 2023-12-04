package net.brutewars.sandbox.config;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.*;
import net.brutewars.sandbox.bworld.world.size.WorldSizes;

import java.io.File;

public final class ConfigSettings {
    private final BWorldPlugin plugin;

    @Getter private CommandCooldownParser commandCooldownParser;
    @Getter private BonusChestParser bonusChestParser;
    @Getter private AnimatedMenuParser creatingAnimationParser;
    @Getter private WorldSizeParser worldSizeParser;
    @Getter private SchematicSettingsParser schematicParser;
    @Getter private ConfigParser configParser;

    public ConfigSettings(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();

        bonusChestParser = new BonusChestParser(plugin);
        creatingAnimationParser = new AnimatedMenuParser(plugin, "menu" + File.separator + "creating-animation.yml");
        worldSizeParser = new WorldSizeParser(plugin);
        commandCooldownParser = new CommandCooldownParser(plugin);
        schematicParser = new SchematicSettingsParser(plugin);
        configParser = new ConfigParser(plugin);

        SectionParser[] parsers = new SectionParser[] {
                bonusChestParser,
                creatingAnimationParser,
                commandCooldownParser,
                schematicParser,
                configParser
        };

        for (SectionParser parser : parsers)
            parser.parse();

        Lang.reload(plugin);
        WorldSizes.reload(plugin);
    }

}