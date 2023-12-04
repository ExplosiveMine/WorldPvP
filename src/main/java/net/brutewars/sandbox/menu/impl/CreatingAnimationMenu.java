package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.config.parser.AnimatedMenuParser;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.menus.animation.AnimatedMenu;
import net.brutewars.sandbox.menu.menus.animation.MenuAnimation;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;

public final class CreatingAnimationMenu extends AnimatedMenu {
    private final AnimatedMenuParser parser;

    public CreatingAnimationMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.CREATING_ANIMATION, Lang.CREATING_ANIMATION.get(), InventoryType.HOPPER, plugin.getConfigSettings().getCreatingAnimationParser().getAnimationSpeed());
        this.parser = plugin.getConfigSettings().getCreatingAnimationParser();
    }

    @Override
    public MenuAnimation getAnimation() {
        return new MenuAnimation(parser.getFrames(), parser.getItems());
    }

    @Override
    public void onAnimationComplete(BPlayer bPlayer) {
        bPlayer.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);

        BWorld bWorld = bPlayer.getBWorld();
        if (bWorld != null) {
            bWorld.teleportToWorld(bPlayer);
            Lang.WORLD_CREATED.send(bWorld.getOwner(), bWorld.getSettings().getBorderSize().getSize());
        }
    }

    @Override
    public void placeItems() {
        //noop
    }

}