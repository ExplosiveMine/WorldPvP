package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.Command;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CmdCreate implements Command {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("create");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create your own world";
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
        BPlayer bPlayer = plugin.getBPlayerManager().get((Player) sender);

        if (bPlayer.getBWorld() != null) {
            Lang.ALREADY_HAVE_WORLD.send(sender);
            return;
        }

        plugin.getMenuManager().open(MenuIdentifier.CREATE, bPlayer);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

}