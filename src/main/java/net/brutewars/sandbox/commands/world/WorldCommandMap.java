package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandMap;

public final class WorldCommandMap extends CommandMap {
    public WorldCommandMap() {
        super();
    }

    @Override
    public void loadDefaultCommands(final BWorldPlugin plugin) {
        registerCommand(new CmdCreate(plugin));
        registerCommand(new CmdKick());
        registerCommand(new CmdHelp());
        registerCommand(new CmdInvite());
        registerCommand(new CmdAccept());
        registerCommand(new CmdDeny());
        registerCommand(new CmdLeave());
        registerCommand(new CmdReset(plugin));
        registerCommand(new CmdConfirmReset());
        registerCommand(new CmdReload());
        registerCommand(new CmdInfo());
    }

}