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

public final class CmdDeny implements Command {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("deny");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "deny <player-name>";
    }

    @Override
    public String getDescription() {
        return "Reject an invitation.";
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

        Lang.INVITE_DENIED.send(invitee, invitingWorld.getAlias());
        Lang.PLAYER_DENIED_INVITE.send(inviter, invitee.getName());

        invitingWorld.removeInvite(invitee);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getWorldInvitations(plugin, sender);
    }

}