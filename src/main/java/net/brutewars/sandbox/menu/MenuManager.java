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

    public MenuManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadMenus() {
        plugin.getServer().getPluginManager().registerEvents(new MenuListener(plugin), plugin);

        registerMenu(MenuIdentifier.CREATE, new CreateMenu(plugin));
        registerMenu(MenuIdentifier.SETTINGS, new SettingsMenu(plugin));
        registerMenu(MenuIdentifier.RECRUIT, new RecruitMenu(plugin));
        registerMenu(MenuIdentifier.CREATING_ANIMATION, new CreatingAnimationMenu(plugin));
    }

    private void registerMenu(MenuIdentifier menuIdentifier, Menu menu) {
        menus.put(menuIdentifier.getIdentifier(), menu);
    }

    public void openParentMenu(BPlayer bPlayer, String identifier) {
        String parentMenuId = get(identifier).getParentMenuId();

        if (parentMenuId != null && !parentMenuId.isEmpty())
            get(parentMenuId).open(bPlayer);
    }

    public void open(MenuIdentifier menuIdentifier, BPlayer bPlayer) {
        get(menuIdentifier.getIdentifier()).open(bPlayer);
    }

    private Menu get(String identifier) {
        if (menus.isEmpty())
            loadMenus();

        return menus.get(identifier);
    }

}