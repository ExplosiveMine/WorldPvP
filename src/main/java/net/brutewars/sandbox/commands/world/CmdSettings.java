package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.commands.PermissibleCommand;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.player.BPlayer;

import java.util.Collections;
import java.util.List;

public final class CmdSettings implements PermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("settings");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Modify your world's settings!";
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
    public void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        if (bWorld == null)
            return;

        plugin.getMenuManager().open(MenuIdentifier.SETTINGS, bPlayer);
    }

}