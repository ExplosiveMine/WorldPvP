package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;

import java.util.*;

public abstract class CommandMap {

    private final Map<String, Command> subCommands = new LinkedHashMap<>();
    private final Map<String, Command> aliasesToCommand = new HashMap<>();

    protected CommandMap() { }

    public abstract void loadDefaultCommands(BWorldPlugin plugin);

    public void registerCommand(Command command) {
        String label = command.getAliases().get(0).toLowerCase();

        if (subCommands.containsKey(label)) {
            subCommands.remove(label);
            aliasesToCommand.values().removeIf(tCmd -> tCmd.getAliases().get(0).equals(label));
        }

        subCommands.put(label, command);

        for (int i = 1; i < command.getAliases().size(); i++)
            aliasesToCommand.put(command.getAliases().get(i).toLowerCase(), command);
    }

    public Command getCommand(String label) {
        label = label.toLowerCase();
        return subCommands.getOrDefault(label, aliasesToCommand.get(label));
    }

    public List<Command> getSubCommands() {
        return Collections.unmodifiableList(new ArrayList<>(subCommands.values()));
    }

}