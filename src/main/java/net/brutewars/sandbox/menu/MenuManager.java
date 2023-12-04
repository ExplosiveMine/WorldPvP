package net.brutewars.sandbox.menu;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.impl.*;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class MenuManager {
    private final BWorldPlugin plugin;

    private final Map<MenuIdentifier, Menu> menus = new HashMap<>();

    public MenuManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadMenus() {
        plugin.getServer().getPluginManager().registerEvents(new MenuListener(plugin), plugin);

        registerMenu(new CreateMenu(plugin));
        registerMenu(new SettingsMenu(plugin));
        registerMenu(new RecruitMenu(plugin));
        registerMenu(new CreatingAnimationMenu(plugin));
        registerMenu(new SocialMenu(plugin));
        registerMenu(new JoinWorldMenu(plugin));
        registerMenu(new StructureGenMenu(plugin));
    }

    private void registerMenu(Menu menu) {
        menus.put(menu.getIdentifier(), menu);
    }

    public void openParentMenu(BPlayer bPlayer, MenuIdentifier identifier) {
        MenuIdentifier parentIdentifier = identifier.getParentIdentifier();
        if (parentIdentifier != null)
            open(parentIdentifier, bPlayer);
    }

    public void open(MenuIdentifier menuIdentifier, BPlayer bPlayer) {
        getMenu(menuIdentifier).open(bPlayer);
    }

    public void open(MenuIdentifier menuIdentifier, Player player) {
        open(menuIdentifier, plugin.getBPlayerManager().get(player));
    }

    public @NotNull Menu getMenu(MenuIdentifier identifier) {
        return menus.get(identifier);
    }

}