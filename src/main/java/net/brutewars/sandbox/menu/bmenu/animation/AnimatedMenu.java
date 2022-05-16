package net.brutewars.sandbox.menu.bmenu.animation;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.thread.Executor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AnimatedMenu extends Menu {
    private final Map<UUID, MenuAnimation> playerAnimations;
    private final long animationSpeed;

    public AnimatedMenu(BWorldPlugin plugin, String identifier, InventoryType type, String title, String parentMenuId, long animationSpeed) {
        super(plugin, identifier, title, type, parentMenuId);

        this.playerAnimations = new HashMap<>();
        this.animationSpeed = animationSpeed;
    }

    public abstract MenuAnimation getAnimation();

    public abstract void onAnimationComplete(BPlayer BPlayer);

    @Override
    public void open(BPlayer bPlayer) {
        if (bPlayer.isSleeping())
            return;

        Inventory inventory = build(bPlayer);
        bPlayer.openInventory(inventory);

        UUID uuid = bPlayer.getUuid();

        if (playerAnimations.containsKey(uuid))
            return;
        playerAnimations.put(uuid, getAnimation());

        MenuAnimation animation = playerAnimations.get(uuid);

        Executor.sync(plugin, runnable -> {
            if (animation.playNext(bPlayer, inventory))
                return;

            playerAnimations.remove(uuid);
            close(bPlayer, false);
            runnable.cancel();

            onAnimationComplete(bPlayer);
        }, 0L, animationSpeed);
    }

    @Override
    public void close(BPlayer bPlayer, boolean openParentMenu) {
        if (playerAnimations.containsKey(bPlayer.getUuid())) return;
        super.close(bPlayer, openParentMenu);
    }

}