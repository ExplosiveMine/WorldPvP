package net.brutewars.sandbox.menu.bworld;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.bmenu.Menu;
import net.brutewars.sandbox.menu.items.CyclingItem;
import net.brutewars.sandbox.menu.items.ItemFactory;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

public final class SettingsMenu extends Menu {
    public SettingsMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.SETTINGS.getIdentifier(), Lang.SETTINGS_MENU.get(), 27, null);
    }

    @Override
    public void placeItems() {
        CyclingItem gamemode = new CyclingItem(ItemFactory.createItem(Material.BEDROCK, "&3&lGamemode",
                (event, bPlayer) -> bPlayer.runIfOnline(player -> player.setGameMode(GameMode.SURVIVAL)),
                "&8Default gamemode is &d&lCREATIVE", "&8Click to change the default gamemode", "&8to &c&lSURVIVAL"));
        gamemode.add(ItemFactory.createItem(Material.DIAMOND_SWORD, "&3&lGamemode",
                (event, bPlayer) -> bPlayer.runIfOnline(player -> player.setGameMode(GameMode.CREATIVE)),
                "&8Default gamemode is &c&lSURVIVAL", "&8Click to change the default gamemode", "&8to &d&lCREATIVE"));
        gamemode.setStartingIndex(bPlayer -> GameMode.SURVIVAL.equals(bPlayer.getIfOnline(HumanEntity::getGameMode)) ? 1 : 0);
        setItem(10, gamemode);


        CyclingItem difficulty = new CyclingItem(ItemFactory.createItem(Material.SKULL_ITEM, 3, "&4&lDifficulty",
                (event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.EASY, true),
                "&8Current difficulty is &b&lPEACEFUL", "&8Click to change the difficulty", "&8to &a&lEASY"));

        difficulty.add(ItemFactory.createItem(Material.SKULL_ITEM, 2, "&4&lDifficulty",
                (event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.NORMAL, true),
                "&8Current difficulty is &a&lEASY", "&8Click to change the difficulty", "&8to &d&lNORMAL"));

        difficulty.add(ItemFactory.createItem(Material.SKULL_ITEM, 0, "&4&lDifficulty",
                (event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.HARD, true),
                "&8Current difficulty is &d&lNORMAL", "&8Click to change the difficulty", "&8to &c&lHARD"));

        difficulty.add(ItemFactory.createItem(Material.SKULL_ITEM, 1, "&4&lDifficulty",
                (event, bPlayer) -> bPlayer.getBWorld().setDifficulty(Difficulty.PEACEFUL, true),
                "&8Current difficulty is &c&lHARD", "&8Click to change the difficulty", "&8to &b&lPEACEFUL"));

        difficulty.setStartingIndex(bPlayer -> {
            switch (bPlayer.getBWorld().getDifficulty()) {
                case PEACEFUL:
                    return 0;
                case EASY:
                    return 1;
                case NORMAL:
                    return 2;
                case HARD:
                    return 3;
            }
            return 0;
        });
        setItem(12, difficulty);


        CyclingItem cheats = new CyclingItem(ItemFactory.createItem(Material.COMMAND, "&6&lCheats",
                (event, bPlayer) -> bPlayer.getBWorld().setCheating(true),
                "&8Cheats are currently &a&lENABLED", "&8Click to set cheats to &c&lDISABLED"));

        cheats.add(ItemFactory.createItem(Material.BARRIER, "&6&lCheats",
                (event, bPlayer) -> bPlayer.getBWorld().setCheating(false),
                "&8Cheats are currently &c&lDISABLED", "&8Click to set cheats to &a&lENABLED"));

        cheats.setStartingIndex(bPlayer -> bPlayer.getBWorld().isCheating() ? 1 : 0);
        setItem(14, cheats);


        setItem(16, ItemFactory.createItem(Material.MAP, "&2&lRecruit members",
                (event, bPlayer) -> plugin.getMenuManager().open(MenuIdentifier.RECRUIT, bPlayer)));

    }

}