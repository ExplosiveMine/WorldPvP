package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.CommandArguments;
import net.brutewars.worldpvp.commands.CommandTabCompletes;
import net.brutewars.worldpvp.commands.ICommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.rank.RankManager;
import net.brutewars.worldpvp.world.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class CmdAccept implements ICommand {
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
        final BWorld invitingWorld = CommandArguments.getBWorld(plugin, sender, args[1]);

        if (invitingWorld == null)
            return;

        final BPlayer invitee = plugin.getBPlayerManager().getBPlayer((Player) sender);
        final BPlayer inviter = invitingWorld.getInviter(invitee);

        if (inviter == null) {
            Lang.NO_INVITE.send(invitee);
            return;
        }

        if (invitee.getBWorld() != null) {
            Lang.ALREADY_HAVE_WORLD.send(invitee);
            return;
        }

        inviter.getBWorld().addPlayer(invitee, RankManager.getMember());

        Lang.PLAYER_ACCEPTED_INVITE.send(inviter, invitee.getName());
        Lang.SUCCESSFULLY_JOINED_WORLD.send(invitee, invitingWorld.getOwner().getName());
        Lang.NEW_MEMBER.send(invitingWorld, invitee.getName());

        invitingWorld.removeInvite(invitee);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getWorldInvitations(plugin, sender);
    }
}