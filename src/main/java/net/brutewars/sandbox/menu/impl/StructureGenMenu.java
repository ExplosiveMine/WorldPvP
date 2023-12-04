package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.items.builders.SkullBuilder;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.event.inventory.InventoryType;

public final class StructureGenMenu extends Menu {
    public StructureGenMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.STRUCTURE_GEN, Lang.STRUCTURE_GEN_MENU.get(), InventoryType.HOPPER);
    }

    @Override
    public void placeItems() {
        setItem(1, new SkullBuilder()
                .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc0NzJkNjA4ODIxZjQ1YTg4MDUzNzZlYzBjNmZmY2I3ODExNzgyOWVhNWY5NjAwNDFjMmEwOWQxMGUwNGNiNCJ9fX0=")
                .setDisplayName("&a&lConfirm")
                .setLore("&7Your world will generate with",
                        "&7portals, a bonus chest, a beacon",
                        "&7and a spawn area!")
                .onClick((event, bPlayer) -> createBWorld(bPlayer, true)));

        setItem(3, new SkullBuilder()
                .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjk1M2IxMmEwOTQ2YjYyOWI0YzA4ODlkNDFmZDI2ZWQyNmZiNzI5ZDRkNTE0YjU5NzI3MTI0YzM3YmI3MGQ4ZCJ9fX0=")
                .setDisplayName("&c&lCancel")
                .setLore("&7Your world will be untouched.")
                .onClick((event, bPlayer) -> createBWorld(bPlayer, false)));
    }

    public void createBWorld(BPlayer bPlayer, boolean generate) {
        close(bPlayer, false);
        Lang.WORLD_CREATING.send(bPlayer);
        plugin.getBWorldManager().resolveRequest(bPlayer, generate);
        plugin.getMenuManager().open(MenuIdentifier.CREATING_ANIMATION, bPlayer);
    }

}