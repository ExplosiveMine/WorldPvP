package net.brutewars.worldpvp.commands;

import lombok.Getter;
import net.brutewars.worldpvp.BWorldPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandHandler {
    @Getter protected final String label;

    protected final BWorldPlugin plugin;
    protected final CommandMap commandMap;
    protected PluginCommand command;

    public CommandHandler(final BWorldPlugin plugin, final String label, CommandMap commandMap) {
        this.plugin = plugin;
        this.label = label;
        this.commandMap = commandMap;
    }

    public void setCommand(PluginCommand command, String...aliases) {
        this.command = command;
        command.setAliases(Arrays.asList(aliases));
        ((CraftServer) plugin.getServer()).getCommandMap().register("world", command);
    }

    public void registerCommand(ICommand iCommand) {
        commandMap.registerCommand(iCommand);
    }

    public void unregisterCommand(ICommand iCommand) {
        commandMap.unregisterCommand(iCommand);
    }

    public List<ICommand> getSubCommands() {
        return commandMap.getSubCommands();
    }

    public ICommand getCommand(String commandLabel) {
        return commandMap.getCommand(commandLabel);
    }

    public void dispatchSubCommand(CommandSender sender, String subCommand) {
        dispatchSubCommand(sender, subCommand, "");
    }

    public void dispatchSubCommand(CommandSender sender, String subCommand, String args) {
        String[] argsSplit = args.split(" ");
        String[] commandArguments = new String[argsSplit.length + 1];
        commandArguments[0] = subCommand;
        System.arraycopy(argsSplit, 0, commandArguments, 1, argsSplit.length);
        command.execute(sender, getLabel(), commandArguments);
    }

    protected abstract class PluginCommand extends BukkitCommand {
        protected PluginCommand(String label) {
            super(label);
        }

        @Override
        public abstract boolean execute(CommandSender sender, String label, String[] args);

        @Override
        public List<String> tabComplete(CommandSender sender, String label, String[] args) {
            if (args.length > 0) {
                ICommand command = commandMap.getCommand(args[0]);
                if (command != null) {
                    return command.getPermission() != null && !sender.hasPermission(command.getPermission()) ?
                            new ArrayList<>() : command.tabComplete(plugin, sender, args);
                }
            }

            List<String> list = new ArrayList<>();

            for (ICommand subCommand : getSubCommands()) {
                if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                    for (String alias : subCommand.getAliases())
                        if (alias.contains(args[0].toLowerCase())) list.add(alias);
                }
            }

            return list;
        }

    }

}