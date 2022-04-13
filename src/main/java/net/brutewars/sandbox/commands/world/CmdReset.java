package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.IPermissibleCommand;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.BWorld;

import java.util.Arrays;
import java.util.List;

public final class CmdReset implements IPermissibleCommand {
    final long cooldown;

    public CmdReset(final BWorldPlugin plugin) {
        this.cooldown = plugin.getConfig().getLong("commands.cooldown.reset") * 1000;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("reset", "delete", "disband");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Reset your world permanently.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public boolean displayCommand() {
        return true;
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        Lang.CONFIRM_RESET.send(bPlayer);
        bWorld.initialiseReset();
    }

}