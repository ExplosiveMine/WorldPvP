package net.brutewars.sandbox.menu.bmenu.bworld;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.bmenu.pagination.PaginatedMenu;
import net.brutewars.sandbox.menu.items.ItemFactory;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public final class RecruitMenu extends PaginatedMenu {
    public RecruitMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.RECRUIT.getIdentifier(), Lang.RECRUIT_MENU.get(),54,"settings");
    }

    @Override
    public void init() {
        populate(plugin.getServer().getOnlinePlayers().stream().map(player -> ItemFactory.createSkull(player.getDisplayName(), player, (event, bPlayer) -> {
            final TextComponent textComponent = new TextComponent(Lang.CLICK_TO_INVITE.get(player.getDisplayName()));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sandbox invite " + player.getName()));
            bPlayer.runIfOnline(_player -> _player.spigot().sendMessage(textComponent));
        })).iterator(), 45, 48, 50);
    }

    @Override
    public boolean reloadOnOpen() {
        return true;
    }

}