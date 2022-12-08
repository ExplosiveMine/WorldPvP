package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.menu.items.builders.SkullBuilder;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.WorldType;
import org.bukkit.event.inventory.InventoryType;

public final class CreateMenu extends Menu {
    public CreateMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.CREATE, Lang.CREATE_MENU.get(), InventoryType.HOPPER);
    }

    @Override
    public void placeItems() {
        setItem(1, new SkullBuilder()
                .setDisplayName("&5&lAmplified")
                .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWMxMWQ2Yzc5YjhhMWYxODkwMmQ3ODNjZGRhNGJkZmI5ZDQ3MzM3YjczNzkxMDI4YTEyNmE2ZTZjZjEwMWRlZiJ9fX0=")
                .setLore("&dA world from the past with amplified terrain!")
                .onClick((event, bPlayer) -> createWorld(bPlayer, WorldType.AMPLIFIED)));

        setItem(2, new SkullBuilder()
                .setDisplayName("&4&lFlat")
                .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDUyOGVkNDU4MDI0MDBmNDY1YjVjNGUzYTZiN2E5ZjJiNmE1YjNkNDc4YjZmZDg0OTI1Y2M1ZDk4ODM5MWM3ZCJ9fX0=")
                .setLore("&cYour world will be completely flat!")
                .onClick((event, bPlayer) -> createWorld(bPlayer, WorldType.FLAT)));

        setItem(3, new SkullBuilder()
                .setDisplayName("&6&lRegular")
                .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y0MDk0MmYzNjRmNmNiY2VmZmNmMTE1MTc5NjQxMDI4NmE0OGIxYWViYTc3MjQzZTIxODAyNmMwOWNkMSJ9fX0=")
                .setLore("&eYour world will be as usual!")
                .onClick((event, bPlayer) -> createWorld(bPlayer, WorldType.NORMAL)));
    }

    private void createWorld(BPlayer bPlayer, WorldType type) {
        close(bPlayer, false);
        Lang.WORLD_CREATING.send(bPlayer);
        plugin.getBWorldManager().createBWorld(bPlayer, type);
        plugin.getMenuManager().open(MenuIdentifier.CREATING_ANIMATION, bPlayer);
    }

}