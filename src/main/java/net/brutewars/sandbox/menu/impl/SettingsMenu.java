package net.brutewars.sandbox.menu.impl;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.menu.MenuIdentifier;
import net.brutewars.sandbox.menu.menus.Menu;
import net.brutewars.sandbox.menu.items.builders.CyclingItemBuilder;
import net.brutewars.sandbox.menu.items.builders.ItemBuilder;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;

public final class SettingsMenu extends Menu {
    public SettingsMenu(BWorldPlugin plugin) {
        super(plugin, MenuIdentifier.SETTINGS, Lang.SETTINGS_MENU.get(), 9);
    }

    @Override
    public void placeItems() {
        setItem(0, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.BEDROCK)
                        .setDisplayName("&5&lGamemode")
                        .onClick((inventoryClickEvent, bPlayer) -> setDefaultGameMode(bPlayer, GameMode.SURVIVAL))
                        .setLore("&7Default gamemode is &dCREATIVE", "&7Click to set to &cSURVIVAL")
                ).add(new ItemBuilder(Material.DIAMOND_SWORD)
                        .setDisplayName("&5&lGamemode")
                        .onClick((inventoryClickEvent, bPlayer) -> setDefaultGameMode(bPlayer, GameMode.CREATIVE))
                        .setLore("&7Default gamemode is &cSURVIVAL", "&7Click to set to &dCREATIVE")
                ).setStartingIndex(bPlayer -> GameMode.SURVIVAL == bPlayer.getIfOnline(HumanEntity::getGameMode) ? 1 : 0)
        );

        setItem(1, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .onClick((event, bPlayer) -> setDifficulty(bPlayer, Difficulty.EASY))
                        .setLore("&7Current difficulty is &bPEACEFUL", "&7Click to to &aEASY")
                ).add(new ItemBuilder(Material.ZOMBIE_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .onClick((event, bPlayer) -> setDifficulty(bPlayer, Difficulty.NORMAL))
                        .setLore("&7Current difficulty is &aEASY", "&7Click to set to &dNORMAL")
                ).add(new ItemBuilder(Material.SKELETON_SKULL)
                        .setDisplayName("&4&lDifficulty")
                        .onClick((event, bPlayer) -> setDifficulty(bPlayer, Difficulty.HARD))
                        .setLore("&7Current difficulty is &dNORMAL", "&7Click to set to &eHARD")
                ).add(new ItemBuilder(Material.CREEPER_HEAD)
                        .setDisplayName("&4&lDifficulty")
                        .onClick((event, bPlayer) -> setDifficulty(bPlayer, Difficulty.PEACEFUL))
                        .setLore("&7Current difficulty is &eHARD", "&7Click to set to &bPEACEFUL")
                ).setStartingIndex(bPlayer ->
                        switch (bPlayer.getBWorld().getSettings().getDifficulty()) {
                            case EASY -> 1;
                            case NORMAL -> 2;
                            case HARD -> 3;
                            default -> 0;
                        }
                ));

        setItem(2, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.ROTTEN_FLESH)
                        .setDisplayName("&6&lAnimals")
                        .setLore("&7Animals are set to &aTRUE", "&7Click to set to &cFALSE")
                        .onClick((event, bPlayer) -> setAnimals(bPlayer, false)))
                .add(new ItemBuilder(Material.BEETROOT_SOUP)
                        .setDisplayName("&6&lAnimals")
                        .setLore("&7Animals are set to &cFALSE", "&7Click to set to &aTRUE")
                        .onClick((event, bPlayer) -> setAnimals(bPlayer, true)))
                .setStartingIndex(bPlayer -> bPlayer.getBWorld().getSettings().isAnimals() ? 0 : 1));

        setItem(3, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.FERMENTED_SPIDER_EYE)
                        .setDisplayName("&9&lMonsters")
                        .setLore("&7Monsters are set to &4AGGRESSIVE", "&7Click to set monsters to &bPASSIVE")
                        .onClick((event, bPlayer) -> setAggressiveMonsters(bPlayer, false)))
                .add(new ItemBuilder(Material.CAULDRON)
                        .setDisplayName("&9&lMonsters")
                        .setLore("&7Monsters are set to &bPASSIVE", "&7Click to set monsters to &4AGGRESSIVE")
                        .onClick((event, bPlayer) -> setAggressiveMonsters(bPlayer, true)))
                .setStartingIndex(bPlayer -> bPlayer.getBWorld().getSettings().isAggressiveMonsters() ? 0 : 1));

        setItem(4, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.WOODEN_AXE)
                        .setDisplayName("&3&lPlayers Can Build")
                        .setLore("&7Players can build is set to &aTRUE", "&7Click to set to &cFALSE")
                        .onClick((event, bPlayer) -> setPlayersCanBuild(bPlayer, false)))
                .add(new ItemBuilder(Material.FLINT_AND_STEEL)
                        .setDisplayName("&3&lPlayers Can Build")
                        .setLore("&7Players can build is set to &cFALSE", "&7Click to set to &aTRUE")
                        .onClick((event, bPlayer) -> setPlayersCanBuild(bPlayer, true)))
                .setStartingIndex(bPlayer -> bPlayer.getBWorld().getSettings().isPlayersCanBuild() ? 0 : 1));

        setItem(5, new CyclingItemBuilder(plugin)
                .add(new ItemBuilder(Material.TNT)
                        .setDisplayName("&d&lKeep Inventory")
                        .setLore("&7Keep inventory is set to &cFALSE", "&7Click to set to &aTRUE")
                        .onClick((event, bPlayer) -> setKeepInventory(bPlayer, true))
                ).add(new ItemBuilder(Material.SLIME_BLOCK)
                        .setDisplayName("&d&lKeep Inventory")
                        .setLore("&7Keep inventory is set to &aTRUE", "&7Click to set to &cFALSE")
                        .onClick((event, bPlayer) -> setKeepInventory(bPlayer, false))
                ).setStartingIndex(bPlayer -> !bPlayer.getBWorld().getSettings().isKeepInventory() ? 0 : 1));

        setItem(6, new ItemBuilder(Material.MAP)
                .setDisplayName("&2&lRecruit Members")
                .setLore("&7Invite your friends!")
                .onClick((event, bPlayer) -> plugin.getMenuManager().open(MenuIdentifier.RECRUIT, bPlayer)));

        setItem(8, new ItemBuilder(Material.RED_MUSHROOM_BLOCK)
                .setDisplayName("&4&lRecycle World")
                .setLore("&cWARNING: This will delete your world permanently!")
                .onClick((event, bPlayer) -> {
                    bPlayer.runIfOnline(player -> player.performCommand("world reset"));
                    close(bPlayer, false);
                }));

    }

    private void setDefaultGameMode(BPlayer bPlayer, GameMode gameMode) {
        bPlayer.getBWorld().getSettings().setDefaultGameMode(gameMode);
        onUpdateSetting(bPlayer);
    }

    private void setDifficulty(BPlayer bPlayer, Difficulty difficulty) {
        bPlayer.getBWorld().getSettings().setDifficulty(difficulty);
        onUpdateSetting(bPlayer);
    }

    private void setAnimals(BPlayer bPlayer, boolean animals) {
        bPlayer.getBWorld().getSettings().setAnimals(animals);
        onUpdateSetting(bPlayer);
    }

    private void setAggressiveMonsters(BPlayer bPlayer, boolean aggressiveMonsters) {
        bPlayer.getBWorld().getSettings().setAggressiveMonsters(aggressiveMonsters);
        onUpdateSetting(bPlayer);
    }

    private void setPlayersCanBuild(BPlayer bPlayer, boolean playersCanBuild) {
        bPlayer.getBWorld().getSettings().setPlayersCanBuild(playersCanBuild);
        onUpdateSetting(bPlayer);
    }

    private void setKeepInventory(BPlayer bPlayer, boolean keepInventory) {
        bPlayer.getBWorld().getSettings().setKeepInventory(keepInventory);
        onUpdateSetting(bPlayer);
    }

    private void onUpdateSetting(BPlayer bPlayer) {
        bPlayer.getBWorld().applySandboxSettings();
        bPlayer.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
    }

}