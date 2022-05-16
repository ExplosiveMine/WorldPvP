package net.brutewars.sandbox.menu.bworld;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.AnimatedMenuParser;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.bmenu.animation.AnimatedMenu;
import net.brutewars.sandbox.menu.bmenu.animation.MenuAnimation;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;

public final class CreatingAnimationMenu extends AnimatedMenu {
    private final AnimatedMenuParser parser;

    public CreatingAnimationMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.CREATING_ANIMATION.getIdentifier(), InventoryType.HOPPER, Lang.CREATING_ANIMATION.get(), null, plugin.getConfigSettings().creatingAnimationParser.getAnimationSpeed());
        parser = plugin.getConfigSettings().creatingAnimationParser;
    }

    @Override
    public void placeItems() {
        //noop
    }

    @Override
    public MenuAnimation getAnimation() {
        return new MenuAnimation(parser.getFrames(), parser.getItems());
    }

    @Override
    public void onAnimationComplete(BPlayer bPlayer) {
        bPlayer.playSound(Sound.ORB_PICKUP, 100, 0);
        bPlayer.getBWorld().teleportToWorld(bPlayer);
    }

}