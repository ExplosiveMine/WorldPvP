package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandArguments;
import net.brutewars.sandbox.commands.CommandTabCompletes;
import net.brutewars.sandbox.commands.IPermissibleCommand;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.JSONMessage;
import net.brutewars.sandbox.utils.StringUtils;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CmdInvite implements IPermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("invite");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "invite <player-name>";
    }

    @Override
    public String getDescription() {
        return "Invite a player to join your world.";
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
    public Consumer<BPlayer> getPermissionLackAction() {
        return Lang.NO_PERMISSION_INVITE::send;
    }

    @Override
    public Predicate<BPlayer> getPredicate() {
        return bPlayer -> bPlayer.getRank().isOwner();
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer inviter, BWorld bWorld, String[] args) {
        final BPlayer invitee = CommandArguments.getBPlayer(plugin, inviter, args[1]);
        if (invitee == null) return;

        if (bWorld.getPlayers(true).contains(invitee)) {
            Lang.INVITED_PLAYER_IS_MEMBER.send(inviter);
            return;
        }

        if (bWorld.isInvited(invitee)) {
            Lang.PLAYER_ALREADY_INVITED.send(inviter);
            return;
        }

        final String worldName = bWorld.getOwner().getName();
        JSONMessage.create(Lang.PLAYER_INVITED.get(worldName))
                .then(StringUtils.colour(" &aACC. "))
                    .tooltip(Lang.ACCEPT_INVITE_TOOLTIP.get())
                    .runCommand("/world accept " + worldName)
                .then(StringUtils.colour("&cDECL. "))
                    .tooltip(Lang.DENY_INVITE_TOOLTIP.get())
                    .runCommand("/world deny " + worldName)
                .send(invitee);

        bWorld.invite(inviter, invitee);
        Lang.SUCCESSFULLY_INVITED_PLAYER.send(inviter, invitee.getName());
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getOnlinePlayersWithoutWorlds(plugin);
    }
}