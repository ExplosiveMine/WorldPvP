package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.commands.CommandArguments;
import net.brutewars.sandbox.commands.CommandTabCompletes;
import net.brutewars.sandbox.commands.ICommand;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public final class CmdInfo implements ICommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("info");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "info <player-name>";
    }

    @Override
    public String getDescription() {
        return "Get info about your world or a player's world.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        final BPlayer bPlayer = CommandArguments.getBPlayer(plugin, sender);

        BWorld bWorld;
        if (args.length == 2)
            bWorld = CommandArguments.getBWorld(plugin, sender, args[1]);
        else
            bWorld = bPlayer.getBWorld();

        if (bWorld == null)
            return;

        Lang.WORLD_INFO.send(bPlayer, bWorld.getAlias(), bWorld.getWorldSize().getValue());
        for (BPlayer _bPlayer : bWorld.getPlayers(false))
            Lang.MEMBER_LIST.send(bPlayer, _bPlayer.getName());
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getBWorlds(plugin);
    }

}