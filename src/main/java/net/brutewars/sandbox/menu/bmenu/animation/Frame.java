package net.brutewars.sandbox.menu.bmenu.animation;

import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

import java.util.function.Function;

public final class Frame {
    private final String[] pattern;

    public Frame(String[] pattern) {
        this.pattern = pattern;
    }

    public void play(BPlayer bPlayer, Inventory inventory, Function<String, MenuItem> itemProvider) {
        bPlayer.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f);

        for (int i = 0; i < pattern.length; i++)
            inventory.setItem(i, itemProvider.apply(pattern[i]).getItem(bPlayer));

        bPlayer.openInventory(inventory);
    }

}