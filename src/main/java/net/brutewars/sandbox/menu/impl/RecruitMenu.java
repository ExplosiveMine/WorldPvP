package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import net.brutewars.sandbox.menu.menus.pagination.PaginatedMenu;
import net.brutewars.sandbox.menu.items.builders.SkullBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;

public final class RecruitMenu extends PaginatedMenu {
    public RecruitMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.RECRUIT, Lang.RECRUIT_MENU.get(), 54);
    }

    @Override
    public void placeItems() {
        populate(plugin.getServer().getOnlinePlayers().stream().map(player -> new SkullBuilder()
                .setDisplayName(player.getDisplayName())
                .setOwner(plugin.getBPlayerManager().get(player))
                .setAction((event, bPlayer) -> {
                    TextComponent textComponent = new TextComponent(Lang.CLICK_TO_INVITE.get(player.getDisplayName()));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sandbox invite " + player.getName()));
                    bPlayer.runIfOnline(_player -> _player.spigot().sendMessage(textComponent));
                })).iterator(), 45, 48, 50);

        setItem(49, new ItemBuilder(Material.RED_BED)
                .setDisplayName("&6Go Back")
                .setAction((event, bPlayer) -> close(bPlayer, true)));
    }

    @Override
    public boolean reloadOnOpen() {
        return true;
    }

}