package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.ICommand;
import net.brutewars.sandbox.menu.MenuIdentifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdSocial implements ICommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("social");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "social";
    }

    @Override
    public String getDescription() {
        return "Opens a menu containing website and discord links";
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
    public void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        plugin.getMenuManager().open(MenuIdentifier.SOCIAL, (Player) sender);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
