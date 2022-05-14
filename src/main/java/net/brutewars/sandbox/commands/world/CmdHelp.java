package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandHandler;
import net.brutewars.sandbox.commands.ICommand;
import net.brutewars.sandbox.config.parser.Lang;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public final class CmdHelp implements ICommand {
    private final Map<ICommand, String> iCommands = new HashMap<>();

    private final Comparator<ICommand> comparator = Comparator.comparing(this::getLabel).thenComparing(o -> o.getAliases().get(0));

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("help");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "help [page]";
    }

    @Override
    public String getDescription() {
        return "List of all commands.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        int page = 1;

        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (IllegalArgumentException ex) {
                Lang.INVALID_AMOUNT.send(sender, args[1]);
                return;
            }
        }

        if (page <= 0) {
            Lang.INVALID_AMOUNT.send(sender, page);
            return;
        }

        List<ICommand> subCommands = getICommands(plugin).keySet().stream()
                .filter(subCommand -> subCommand.getPermission().isEmpty() || sender.hasPermission(subCommand.getPermission()))
                .sorted(comparator)
                .collect(Collectors.toList());


        if (subCommands.isEmpty()) {
            Lang.PLAYER_NO_PERMISSION.send(sender);
            return;
        }

        int lastPage = subCommands.size() / 7;
        if (subCommands.size() % 7 != 0) lastPage++;

        if (page > lastPage) {
            Lang.INVALID_AMOUNT.send(sender, page);
            return;
        }

        subCommands = subCommands.subList((page - 1) * 7, Math.min(subCommands.size(), page * 7));

        Lang.HELP_HEADER.send(sender, page, lastPage);

        for (final ICommand _subCommand : subCommands) {
            if (_subCommand.displayCommand() && (_subCommand.getPermission().isEmpty() || sender.hasPermission(_subCommand.getPermission()))) {
                String description = _subCommand.getDescription();
                Lang.HELP_LINE.send(sender, getLabel(_subCommand) + " " +  _subCommand.getUsage(), description == null ? "" : description);
            }
        }

        if (page != lastPage)
            Lang.HELP_NEXT_PAGE.send(sender, page + 1);
        else
            Lang.HELP_FOOTER.send(sender);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        final List<String> list = new ArrayList<>();

        if (args.length == 2) {
            List<ICommand> subCommands = getICommands(plugin).keySet().stream()
                    .filter(subCommand -> subCommand.displayCommand() && (subCommand.getPermission().isEmpty() || sender.hasPermission(subCommand.getPermission())))
                    .sorted(comparator)
                    .collect(Collectors.toList());

            int lastPage = subCommands.size() / 7;
            if (subCommands.size() % 7 != 0) lastPage++;

            for (int i = 1; i <= lastPage; i++)
                list.add(i + "");
        }

        return list;
    }

    private String getLabel(ICommand iCommand) {
        return iCommands.get(iCommand);
    }

    private Map<ICommand, String> getICommands(final BWorldPlugin plugin) {
        if (iCommands.isEmpty()) {
            for (CommandHandler commandHandler : plugin.getCommands())
                commandHandler.getSubCommands().forEach(iCommand -> iCommands.put(iCommand, commandHandler.getLabel()));
        }
        return iCommands;
    }

}