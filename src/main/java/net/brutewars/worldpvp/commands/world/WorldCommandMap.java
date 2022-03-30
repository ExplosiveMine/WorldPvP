package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.CommandMap;

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