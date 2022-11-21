package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.menu.items.builders.CyclingItemBuilder;
import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

public final class SettingsMenu extends Menu {
    public SettingsMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.SETTINGS, Lang.SETTINGS_MENU.get(), 27);
    }

    @Override
    public void placeItems() {
        setItem(10, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.BEDROCK)
                        .setDisplayName("&3&lGamemode")
                        .setAction((event, bPlayer) -> bPlayer.runIfOnline(player -> player.setGameMode(GameMode.SURVIVAL)))
                        .setLore("&7Default gamemode is &d&lCREATIVE", "&7Click to change the default gamemode", "&7to &c&lSURVIVAL")
                ).add(new ItemBuilder(Material.DIAMOND_SWORD)
                        .setDisplayName("&3&lGamemode")
                        .setAction((event, bPlayer) -> bPlayer.runIfOnline(player -> player.setGameMode(GameMode.CREATIVE)))
                        .setLore("&7Default gamemode is &c&lSURVIVAL", "&7Click to change the default gamemode", "&7to &d&lCREATIVE")
                ).setStartingIndex(bPlayer -> GameMode.SURVIVAL.equals(bPlayer.getIfOnline(HumanEntity::getGameMode)) ? 1 : 0)
        );

        setItem(12, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.EASY, true))
                        .setLore("&7Current difficulty is &b&lPEACEFUL", "&7Click to change the difficulty", "&7to &a&lEASY")
                ).add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.NORMAL, true))
                        .setLore("&7Current difficulty is &a&lEASY", "&7Click to change the difficulty", "&7to &d&lNORMAL")
                ).add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.HARD, true))
                        .setLore("&7Current difficulty is &d&lNORMAL", "&7Click to change the difficulty", "&7to &c&lHARD")
                ).add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.PEACEFUL, true))
                        .setLore("&7Current difficulty is &c&lHARD", "&7Click to change the difficulty", "&7to &b&lPEACEFUL")
                ).setStartingIndex(bPlayer ->
                        switch (bPlayer.getBWorld().getDifficulty()) {
                            case EASY -> 1;
                            case NORMAL -> 2;
                            case HARD -> 3;
                            default -> 0;
                        }
                ));

        setItem(14, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.COMMAND_BLOCK)
                        .setDisplayName("&6&lCheats")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setCheating(true))
                        .setLore("&7Cheats are currently &a&lENABLED", "&7Click to set cheats to &c&l✕ DISABLED")
                ).add(new ItemBuilder(Material.BARRIER)
                        .setDisplayName("&6&lCheats")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setCheating(false))
                        .setLore("&7Cheats are currently &c&lDISABLED", "&7Click to set cheats to &a&l✓ ENABLED")
                ).setStartingIndex(bPlayer -> bPlayer.getBWorld().isCheating() ? 1 : 0));

        setItem(16, new ItemBuilder(Material.MAP)
                .setDisplayName("&2&lRecruit members")
                .setAction((event, bPlayer) -> plugin.getMenuManager().open(MenuIdentifier.RECRUIT, bPlayer)));

        setItem(26, new ItemBuilder(Material.RED_MUSHROOM_BLOCK)
                .setDisplayName("&4&lRecycle world")
                .setAction((event, bPlayer) -> {
                    bPlayer.runIfOnline(player -> player.performCommand("world reset"));
                    close(bPlayer, false);
                }));
    }

}