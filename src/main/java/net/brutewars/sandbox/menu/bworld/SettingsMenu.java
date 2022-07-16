package net.brutewars.sandbox.menu.bworld;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.bmenu.Menu;
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
                        .setLore("&8Default gamemode is &d&lCREATIVE", "&8Click to change the default gamemode", "&8to &c&lSURVIVAL")
                ).add(new ItemBuilder(Material.DIAMOND_SWORD)
                        .setDisplayName("&3&lGamemode")
                        .setAction((event, bPlayer) -> bPlayer.runIfOnline(player -> player.setGameMode(GameMode.SURVIVAL)))
                        .setLore("&8Default gamemode is &c&lSURVIVAL", "&8Click to change the default gamemode", "&8to &d&lCREATIVE")
                ).setStartingIndex(bPlayer -> GameMode.SURVIVAL.equals(bPlayer.getIfOnline(HumanEntity::getGameMode)) ? 1 : 0)
        );

        setItem(12, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.EASY, true))
                        .setLore("&8Current difficulty is &b&lPEACEFUL", "&8Click to change the difficulty", "&8to &a&lEASY")
                ).add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.NORMAL, true))
                        .setLore("&8Current difficulty is &a&lEASY", "&8Click to change the difficulty", "&8to &d&lNORMAL")
                ).add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.HARD, true))
                        .setLore("&8Current difficulty is &d&lNORMAL", "&8Click to change the difficulty", "&8to &c&lHARD")
                ).add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.PEACEFUL, true))
                        .setLore("&8Current difficulty is &c&lHARD", "&8Click to change the difficulty", "&8to &b&lPEACEFUL")
                ).setStartingIndex(bPlayer -> {
                    switch (bPlayer.getBWorld().getDifficulty()) {
                        case EASY:
                            return 1;
                        case NORMAL:
                            return 2;
                        case HARD:
                            return 3;
                        default:
                            return 0;
                    }
                }));

        setItem(14, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.COMMAND_BLOCK)
                        .setDisplayName("&6&lCheats")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setCheating(true))
                        .setLore("&8Cheats are currently &a&lENABLED", "&8Click to set cheats to &c&lDISABLED")
                ).add(new ItemBuilder(Material.BARRIER)
                        .setDisplayName("&6&lCheats")
                        .setAction((event, bPlayer) -> bPlayer.getBWorld().setCheating(false))
                        .setLore("&8Cheats are currently &c&lDISABLED", "&8Click to set cheats to &a&lENABLED")
                ).setStartingIndex(bPlayer -> bPlayer.getBWorld().isCheating() ? 1 : 0));

        setItem(16, new ItemBuilder(Material.MAP)
                .setDisplayName("&2&lRecruit members")
                .setAction((event, bPlayer) -> plugin.getMenuManager().open(MenuIdentifier.RECRUIT, bPlayer)));
    }

}