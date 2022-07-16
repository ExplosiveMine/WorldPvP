package net.brutewars.sandbox.menu.bworld;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.bmenu.animation.AnimatedMenu;
import net.brutewars.sandbox.menu.bmenu.animation.MenuAnimation;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;

public final class CreatingAnimationMenu extends AnimatedMenu {

    public CreatingAnimationMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.CREATING_ANIMATION, Lang.CREATING_ANIMATION.get(), InventoryType.HOPPER, plugin.getConfigSettings().creatingAnimationParser.getAnimationSpeed());
    }

    @Override
    public MenuAnimation getAnimation() {
        return new MenuAnimation(plugin.getConfigSettings().creatingAnimationParser.getFrames(), plugin.getConfigSettings().creatingAnimationParser.getItems());
    }

    @Override
    public void onAnimationComplete(BPlayer bPlayer) {
        bPlayer.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);
        bPlayer.getBWorld().teleportToWorld(bPlayer);
    }

    @Override
    public void placeItems() {
        //noop
    }

}