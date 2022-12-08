package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandArguments;
import net.brutewars.sandbox.commands.CommandTabCompletes;
import net.brutewars.sandbox.commands.Command;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class CmdAccept implements Command {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("accept");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "accept <player-name>";
    }

    @Override
    public String getDescription() {
        return "Accept an invitation and join that world.";
    }

    @Override
    public int getMinArgs() {
        return 2;
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
        BWorld invitingWorld = CommandArguments.getBWorld(plugin, sender, args[1]);

        if (invitingWorld == null)
            return;

        BPlayer invitee = plugin.getBPlayerManager().get((Player) sender);
        BPlayer inviter = invitingWorld.getInviter(invitee);

        if (inviter == null) {
            Lang.NO_INVITE.send(invitee);
            return;
        }

        invitingWorld.addPlayer(invitee, null);
        invitingWorld.teleportToWorld(invitee);

        Lang.PLAYER_ACCEPTED_INVITE.send(inviter, invitee.getName());
        Lang.SUCCESSFULLY_JOINED_WORLD.send(invitee, invitingWorld.getAlias());
        Lang.NEW_MEMBER.send(invitingWorld, invitee.getName());

        invitingWorld.removeInvite(invitee);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getWorldInvitations(plugin, sender);
    }

}