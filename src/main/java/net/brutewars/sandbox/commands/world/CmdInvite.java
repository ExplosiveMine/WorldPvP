package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandArguments;
import net.brutewars.sandbox.commands.CommandTabCompletes;
import net.brutewars.sandbox.commands.Command;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Pair;
import net.brutewars.sandbox.utils.StringUtils;
import net.brutewars.sandbox.bworld.BWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public final class CmdInvite implements Command {
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

        BWorld bWorld = pair.getKey();
        if (bWorld == null)
            return;

        BPlayer invitee = CommandArguments.getBPlayer(plugin, sender, args[1]);
        if (invitee == null)
            return;

        BPlayer inviter = pair.getValue();
        if (bWorld.getMembers(true).contains(invitee)) {
            Lang.INVITED_PLAYER_IS_MEMBER.send(inviter);
            return;
        }

        if (bWorld.isInvited(invitee)) {
            Lang.PLAYER_ALREADY_INVITED.send(inviter);
            return;
        }

        String worldName = bWorld.getAlias();
        TextComponent textComponent = Component.text()
                .append(Component.text(Lang.PLAYER_INVITED.get(worldName)))
                .append(Component.text()
                        .append(Component.text(StringUtils.colour(" &aACC. ")))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(Lang.ACCEPT_INVITE_TOOLTIP.get())))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/world accept " + worldName)))
                .append(Component.text()
                        .append(Component.text(StringUtils.colour("&cDECL. ")))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(Lang.DENY_INVITE_TOOLTIP.get())))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/world deny " + worldName)))
                .build();

        invitee.runIfOnline(player -> player.sendMessage(textComponent));

        bWorld.invite(inviter, invitee);
        Lang.SUCCESSFULLY_INVITED_PLAYER.send(inviter, invitee.getName());
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getOnlinePlayers(plugin);
    }

}