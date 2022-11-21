package net.brutewars.sandbox.database;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.BLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.BWorldManager;
import org.bukkit.Difficulty;

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
                "`player_id` TEXT PRIMARY KEY");

        database.createTable("worlds",
                "`bWorld_id` TEXT NOT NULL",
                "`owner_id` TEXT PRIMARY KEY",
                "`difficulty` TEXT NOT NULL",
                "'default_location' TEXT NOT NULL");

        database.createTable("members",
                "`bWorld_id` TEXT NOT NULL",
                "`player_id` TEXT NOT NULL",
                "'last_location' TEXT NOT NULL");

        database.createTable("settings",
                "'renew_time' INTEGER");
    }

    // loading everything from the database
    // Runs synchronously on enable
    public void load() {
        createSQLiteDatabase();

        // first we load all players
        database.getTable("players").select(playerSet ->  {
            try {
                while (playerSet.next()) {
                    UUID uuid = UUID.fromString(playerSet.getString("player_id"));
                    bPlayerManager.create(uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // next we load all BWorlds
        database.getTable("worlds").select(worldSet -> {
            try {
                while (worldSet.next()) {
                    // getMenu basic BWorld info
                    UUID worldUuid = UUID.fromString(worldSet.getString("bWorld_id"));
                    BPlayer owner = bPlayerManager.get(UUID.fromString(worldSet.getString("owner_id")));
                    BWorld bWorld = bWorldManager.loadBWorld(worldUuid, owner, null);

                    bWorld.setDifficulty(Difficulty.valueOf(worldSet.getString("difficulty")), false);
                    bWorld.setDefaultLocation(new BLocation(worldSet.getString("default_location")));

                    // getMenu members
                    database.getTable("members").select("*", "`bWorld_id` = \"" + worldUuid + "\"", memberSet -> {
                        try {
                            while (memberSet.next())
                                bWorld.addPlayer(bPlayerManager.get(UUID.fromString(memberSet.getString("player_id"))),
                                        new BLocation(memberSet.getString("last_location")));
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
                    bWorldManager.getWorldManager().setLastRoster(resultSet.getLong("renew_time"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        database.closeConnection();
    }

    // Runs synchronously on disable
    public void save() {
        database.backup();

        // create new database since we made a backup...
        createSQLiteDatabase();

        // insert data into tables
        bPlayerManager.getBPlayers().stream().map(BPlayer::getUuid).forEach(uuid -> database.getTable("players").insert(uuid.toString()));
        database.getTable("settings").insert(bWorldManager.getWorldManager().getLastRoster());
        bWorldManager.getBWorlds().forEach(bWorld -> {
            String bWorldUuid = bWorld.getUuid().toString();
            database.getTable("worlds").insert(bWorldUuid, bWorld.getOwner().getUuid().toString(), bWorld.getDifficulty().toString(), bWorld.getDefaultLocation().toString());
            bWorld.getPlayers(false).forEach(bPlayer -> database.getTable("members").insert(bWorldUuid, bPlayer.getUuid().toString(), bWorld.getLastLocation(bPlayer).toString()));
        });

        database.closeConnection();
    }

}