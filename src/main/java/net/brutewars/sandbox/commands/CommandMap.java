package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;

import java.util.*;

public abstract class CommandMap {

    private final Map<String, ICommand> subCommands = new LinkedHashMap<>();
    private final Map<String, ICommand> aliasesToCommand = new HashMap<>();

    protected CommandMap() { }

    public abstract void loadDefaultCommands(BWorldPlugin plugin);

    public void registerCommand(ICommand iCommand) {
        String label = iCommand.getAliases().get(0).toLowerCase();

        if (subCommands.containsKey(label)) {
            subCommands.remove(label);
            aliasesToCommand.values().removeIf(tCmd -> tCmd.getAliases().get(0).equals(label));
        }

        subCommands.put(label, iCommand);

        for (int i = 1; i < iCommand.getAliases().size(); i++)
            aliasesToCommand.put(iCommand.getAliases().get(i).toLowerCase(), iCommand);
    }

    public ICommand getCommand(String label) {
        label = label.toLowerCase();
        return subCommands.getOrDefault(label, aliasesToCommand.get(label));
    }

    public List<ICommand> getSubCommands() {
        return Collections.unmodifiableList(new ArrayList<>(subCommands.values()));
    }

}