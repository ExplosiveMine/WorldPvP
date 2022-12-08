package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.items.builders.SkullBuilder;
import net.brutewars.sandbox.menu.menus.Menu;

public final class SocialMenu extends Menu {
    public SocialMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.SOCIAL, Lang.SOCIAL_MENU.get(), 27);
    }

    @Override
    public void placeItems() {
        setItem(11, new SkullBuilder()
                .setDisplayName("&3&lDiscord")
                .setLore("&7Click to join our discord!")
                .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM5ZWU3MTU0OTc5YjNmODc3MzVhMWM4YWMwODc4MTRiNzkyOGQwNTc2YTI2OTViYTAxZWQ2MTYzMTk0MjA0NSJ9fX0=")
                .onClick((event, bPlayer) -> Lang.DISCORD_LINK.send(bPlayer)));

        setItem(15, new SkullBuilder()
                .setDisplayName("&6&lWebsite")
                .setLore("&7Click to visit our website")
                .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTgyMDg2ZDE1NDVhZTg4OGFhNzY2ZjhlZDljNjZlNDc1NWI0MmVkM2E3YmU0ZTBjZmEwNjhkN2Y2NzZkNmRmIn19fQ==")
                .onClick((event, bPlayer) -> Lang.WEBSITE_LINK.send(bPlayer)));
    }

}