package net.brutewars.sandbox.database;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.settings.WorldSettings;
import net.brutewars.sandbox.bworld.world.location.BLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.BWorldManager;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public final class DataManager {
    private final BWorldPlugin plugin;

    private final BPlayerManager bPlayerManager;
    private final BWorldManager bWorldManager;

    private SQLiteDatabase database;

    public DataManager(BWorldPlugin plugin) {
        this.plugin = plugin;
        this.bPlayerManager = plugin.getBPlayerManager();
        this.bWorldManager = plugin.getBWorldManager();
    }

    private void createSQLiteDatabase() {
        File databaseFile = new File(plugin.getDataFolder(), "BruteWars.db");

        // create new file if it does not exist
        try {
            databaseFile.createNewFile();
        } catch (IOException e) {
            Logging.severe("Could not create the database.");
            e.printStackTrace();
        }

        database = new SQLiteDatabase(plugin, databaseFile);

        /*
         * Relationships:
         * World - Owner: One to one
         * Players - Worlds: Many to many
         */

        database.createTable("players",
                "`player_id` TEXT PRIMARY KEY",
                "`hud_slot` INTEGER NOT NULL",
                "`hud_toggled` INTEGER NOT NULL");

        database.createTable("worlds",
                "`bWorld_id` TEXT NOT NULL",
                "`owner_id` TEXT PRIMARY KEY",
                "`world_type` TEXT NOT NULL",
                "'animals' INTEGER NOT NULL",
                "'aggressive_monsters' INTEGER NOT NULL",
                "'allow_build' INTEGER NOT NULL",
                "'keepInventory' INTEGER NOT NULL",
                "`difficulty` TEXT NOT NULL",
                "'gamemode' TEXT NOT NULL");

        database.createTable("members",
                "`bWorld_id` TEXT NOT NULL",
                "`player_id` TEXT NOT NULL",
                "'last_location' TEXT NOT NULL");

        database.createTable("settings",
                "'renew_time' INTEGER NOT NULL");
    }

    public void load() {
        createSQLiteDatabase();

        // first we load all players
        database.getTable("players").select(playerSet ->  {
            try {
                while (playerSet.next()) {
                    UUID uuid = UUID.fromString(playerSet.getString("player_id"));
                    BPlayer bPlayer = bPlayerManager.create(uuid);
                    bPlayer.setHudSlot(playerSet.getInt("hud_slot"));
                    bPlayer.setHudToggled(playerSet.getBoolean("hud_toggled"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // next we load all BWorlds
        database.getTable("worlds").select(worldSet -> {
            try {
                while (worldSet.next()) {
                    // basic BWorld info
                    UUID worldUuid = UUID.fromString(worldSet.getString("bWorld_id"));
                    BPlayer owner = bPlayerManager.get(UUID.fromString(worldSet.getString("owner_id")));
                    WorldType worldType = WorldType.valueOf(worldSet.getString("world_type"));
                    BWorld bWorld = bWorldManager.loadBWorld(worldUuid, owner, worldType, false);

                    WorldSettings settings = bWorld.getSettings();
                    settings.setAnimals(worldSet.getBoolean("animals"));
                    settings.setAggressiveMonsters(worldSet.getBoolean("aggressive_monsters"));
                    settings.setPlayersCanBuild(worldSet.getBoolean("allow_build"));
                    settings.setKeepInventory(worldSet.getBoolean("keepInventory"));
                    settings.setDifficulty(Difficulty.valueOf(worldSet.getString("difficulty")));
                    settings.setDefaultGameMode(GameMode.valueOf(worldSet.getString("gamemode")));

                    // members
                    database.getTable("members").select("*", "`bWorld_id` = \"" + worldUuid + "\"", memberSet -> {
                        try {
                            while (memberSet.next()) {
                                BPlayer bPlayer = bPlayerManager.get(UUID.fromString(memberSet.getString("player_id")));
                                BLocation bLoc = new BLocation(memberSet.getString("last_location"));
                                // Player is owner, so they have already been added; just need to update their last location
                                if (bWorld.getOwner().equals(bPlayer))
                                    bWorld.getLastLocationTracker().updateLastLocation(bPlayer, bLoc);
                                else
                                    bWorld.addMember(bPlayer, bLoc);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        database.getTable("settings").select(resultSet -> {
            try {
                if (resultSet.next())
                    plugin.getWorldRoster().setLastRoster(resultSet.getLong("renew_time"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        database.closeConnection();
    }

    public void save() {
        database.backup();

        createSQLiteDatabase();

        // insert data into tables
        database.getTable("settings").insert(plugin.getWorldRoster().getLastRoster());

        bPlayerManager.getBPlayers().forEach(bPlayer -> database.getTable("players").insert(
                bPlayer.getUuid().toString(),
                bPlayer.getHudSlot(),
                booleanToInt(bPlayer.isHudToggled())
        ));

        bWorldManager.getBWorlds().forEach(bWorld -> {
            String bWorldUuid = bWorld.getUuid().toString();

            WorldSettings settings = bWorld.getSettings();
            database.getTable("worlds").insert(
                    bWorldUuid,
                    bWorld.getOwner().getUuid().toString(),
                    bWorld.getSandboxWorld(World.Environment.NORMAL).getWorldType(),
                    booleanToInt(settings.isAnimals()),
                    booleanToInt(settings.isAggressiveMonsters()),
                    booleanToInt(settings.isPlayersCanBuild()),
                    booleanToInt(settings.isKeepInventory()),
                    settings.getDifficulty().toString(),
                    settings.getDefaultGameMode().toString()
            );

            bWorld.getMembers(true).forEach(bPlayer -> database.getTable("members").insert(
                    bWorldUuid,
                    bPlayer.getUuid().toString(),
                    bWorld.getLastLocationTracker().getLastLocation(bPlayer).toString())
            );
        });

        database.closeConnection();
    }

    private int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

}