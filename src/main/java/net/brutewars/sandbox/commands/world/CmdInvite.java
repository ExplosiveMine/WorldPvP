package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandArguments;
import net.brutewars.sandbox.commands.CommandTabCompletes;
import net.brutewars.sandbox.commands.ICommand;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.JSONMessage;
import net.brutewars.sandbox.utils.Pair;
import net.brutewars.sandbox.utils.StringUtils;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public final class CmdInvite implements ICommand {
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
    public void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        Pair<BWorld, BPlayer> pair = CommandArguments.getPair(plugin, sender);

        BPlayer inviter = pair.getValue();
        BWorld bWorld = pair.getKey();

        if (bWorld == null)
            return;

        BPlayer invitee = CommandArguments.getBPlayer(plugin, sender, args[1]);
        if (invitee == null) return;

        if (bWorld.getPlayers(true).contains(invitee)) {
            Lang.INVITED_PLAYER_IS_MEMBER.send(inviter);
            return;
        }

        if (bWorld.isInvited(invitee)) {
            Lang.PLAYER_ALREADY_INVITED.send(inviter);
            return;
        }

        String worldName = bWorld.getAlias();
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
        return CommandTabCompletes.getOnlinePlayers(plugin);
    }

}