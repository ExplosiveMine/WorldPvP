package net.brutewars.sandbox.config.parser;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.ICommand;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public final class CommandCooldownParser extends SectionParser {
    private final Map<String, Long> commandCooldown = new HashMap<>();

    public CommandCooldownParser(BWorldPlugin plugin) {
        super(plugin, "commands.cooldown");
    }

    @Override
    public void parse() {
        ConfigurationSection cmdCooldownSection = plugin.getConfig().getConfigurationSection(path);
        if (cmdCooldownSection == null)
            return;
        cmdCooldownSection.getKeys(false).forEach(s -> this.commandCooldown.put(s, cmdCooldownSection.getLong(s) * 1000));
    }

    public long get(ICommand iCommand) {
        return commandCooldown.getOrDefault(iCommand.getAliases().get(0), 0L);
    }

}