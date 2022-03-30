package net.brutewars.worldpvp.commands;

import com.google.common.base.Preconditions;
import net.brutewars.worldpvp.BWorldPlugin;

import java.util.*;

public abstract class CommandMap {

    private final Map<String, ICommand> subCommands = new LinkedHashMap<>();
    private final Map<String, ICommand> aliasesToCommand = new HashMap<>();

    protected CommandMap() { }

    public abstract void loadDefaultCommands(final BWorldPlugin plugin);

    public void registerCommand(ICommand iCommand) {
        final String label = iCommand.getAliases().get(0).toLowerCase();

        if (subCommands.containsKey(label)) {
            subCommands.remove(label);
            aliasesToCommand.values().removeIf(tCmd -> tCmd.getAliases().get(0).equals(label));
        }

        subCommands.put(label, iCommand);

        for (int i = 1; i < iCommand.getAliases().size(); i++)
            aliasesToCommand.put(iCommand.getAliases().get(i).toLowerCase(), iCommand);
    }

    public void unregisterCommand(ICommand iCommand) {
        Preconditions.checkNotNull(iCommand, "wCommand parameter cannot be null.");

        final String label = iCommand.getAliases().get(0).toLowerCase();

        subCommands.remove(label);
        aliasesToCommand.values().removeIf(sC -> sC.getAliases().get(0).equals(label));
    }

    public ICommand getCommand(String label) {
        label = label.toLowerCase();
        return subCommands.getOrDefault(label, aliasesToCommand.get(label));
    }

    public List<ICommand> getSubCommands() {
        return Collections.unmodifiableList(new ArrayList<>(subCommands.values()));
    }

}