package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.CommandArguments;
import net.brutewars.worldpvp.commands.CommandTabCompletes;
import net.brutewars.worldpvp.commands.ICommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.world.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class CmdDeny implements ICommand {
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
        final BWorld invitingWorld = CommandArguments.getBWorld(plugin, sender, args[1]);

        if (invitingWorld == null)
            return;

        final BPlayer invitee = plugin.getBPlayerManager().getBPlayer((Player) sender);
        final BPlayer inviter = invitingWorld.getInviter(invitee);

        if (inviter == null) {
            Lang.NO_INVITE.send(invitee);
            return;
        }

        Lang.INVITE_DENIED.send(invitee, invitingWorld.getOwner().getName());
        Lang.PLAYER_DENIED_INVITE.send(inviter, invitee.getName());

        invitingWorld.removeInvite(invitee);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getWorldInvitations(plugin, sender);
    }
}
