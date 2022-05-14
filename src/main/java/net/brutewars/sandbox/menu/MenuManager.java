package net.brutewars.sandbox.menu;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.menu.bworld.CreateMenu;
import net.brutewars.sandbox.menu.bworld.CreatingAnimationMenu;
import net.brutewars.sandbox.menu.bworld.RecruitMenu;
import net.brutewars.sandbox.menu.bworld.SettingsMenu;
import net.brutewars.sandbox.player.BPlayer;

import java.util.HashMap;
import java.util.Map;

public final class MenuManager {
    private final BWorldPlugin plugin;

    private final Map<String, Menu> menus = new HashMap<>();

    public MenuManager(final BWorldPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        new MenuListener(plugin);

        registerMenu(MenuIdentifier.CREATE, new CreateMenu(plugin));
        registerMenu(MenuIdentifier.SETTINGS, new SettingsMenu(plugin));
        registerMenu(MenuIdentifier.RECRUIT, new RecruitMenu(plugin));
        registerMenu(MenuIdentifier.CREATING_ANIMATION, new CreatingAnimationMenu(plugin));
    }

    private void registerMenu(MenuIdentifier menuIdentifier, Menu menu) {
        menus.put(menuIdentifier.getIdentifier(), menu);
    }

    public void openParentMenu(final BPlayer bPlayer, final String identifier) {
        final String parentMenuId = get(identifier).getParentMenuId();

        if (parentMenuId != null && !parentMenuId.isEmpty())
            get(parentMenuId).open(bPlayer);
    }

    public void open(final MenuIdentifier menuIdentifier, final BPlayer bPlayer) {
        get(menuIdentifier.getIdentifier()).open(bPlayer);
    }

    private Menu get(final String identifier) {
        return menus.get(identifier);
    }

}