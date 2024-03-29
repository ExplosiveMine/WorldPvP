package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.PermissibleCommand;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.BWorld;

import java.util.Collections;
import java.util.List;

public final class CmdConfirmReset implements PermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("confirm");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
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
    public boolean displayCommand() {
        return false;
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        if (bWorld == null) {
            Lang.PLAYER_NO_PERMISSION.send(bPlayer);
            return;
        }

        if (bWorld.getResetting() != -1) {
            Lang.RESET_SUCCESS.send(bWorld, bPlayer.getName());
            plugin.getBWorldManager().removeBWorld(bWorld);
        } else {
            Lang.PLAYER_NO_PERMISSION.send(bPlayer);
        }
    }

}