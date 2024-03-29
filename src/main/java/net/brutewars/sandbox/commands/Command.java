package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface Command {
    List<String> getAliases();

    String getPermission();

    String getUsage();

    String getDescription();

    int getMinArgs();

    int getMaxArgs();

    boolean canBeExecutedByConsole();

    void execute(BWorldPlugin plugin, CommandSender sender, String[] args);

    List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args);

    default boolean displayCommand() {
        return true;
    }

}