package net.brutewars.sandbox.config.parser;

import net.brutewars.sandbox.commands.ICommand;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public final class CommandCooldownParser implements SectionParser {
    private final Map<String, Long> commandCooldown = new HashMap<>();

    public CommandCooldownParser(ConfigurationSection configurationSection) {
        parse(configurationSection);
    }

    @Override
    public void parse(ConfigurationSection cmdCooldownSection) {
        cmdCooldownSection.getKeys(false).forEach(s -> this.commandCooldown.put(s, cmdCooldownSection.getLong(s) * 1000));
    }

    public long get(ICommand iCommand) {
        return commandCooldown.getOrDefault(iCommand.getAliases().get(0), 0L);
    }

}