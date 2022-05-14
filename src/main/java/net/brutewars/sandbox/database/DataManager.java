package net.brutewars.sandbox.database;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.LastLocation;
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

    public DataManager(final BWorldPlugin plugin) {
        this.plugin = plugin;
        this.bPlayerManager = plugin.getBPlayerManager();
        this.bWorldManager = plugin.getBWorldManager();
        init();
    }

    // loading everything from the database
    // Runs synchronously on enable
    public void init() {
        createSQLiteDatabase();

        // first we load all players
        database.getTable("players").select(playerSet ->  {
            try {
                while (playerSet.next()) {
                    final UUID uuid = UUID.fromString(playerSet.getString("player_id"));
                    bPlayerManager.createBPlayer(uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // next we load all BWorlds
        database.getTable("worlds").select(worldSet -> {
            try {
                while (worldSet.next()) {
                    // get basic BWorld info
                    final UUID worldUuid = UUID.fromString(worldSet.getString("world_id"));
                    final BPlayer owner = bPlayerManager.getBPlayer(UUID.fromString(worldSet.getString("owner_id")));
                    final BWorld bWorld = bWorldManager.loadBWorld(worldUuid, owner, null);

                    bWorld.setDifficulty(Difficulty.valueOf(worldSet.getString("difficulty")), false);
                    bWorld.setDefaultLocation(new LastLocation(worldSet.getString("default_location")));

                    // get members
                    database.getTable("members").select("*", "`world_id` = \"" + worldUuid + "\"", memberSet -> {
                        try {
                            while (memberSet.next())
                                bWorld.addPlayer(bPlayerManager.getBPlayer(UUID.fromString(memberSet.getString("player_id"))),
                                        new LastLocation(memberSet.getString("last_location")));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //close connection
        database.closeConnection();
    }

    public void createSQLiteDatabase() {
        // we get the database file
        final File databaseFile = new File(plugin.getDataFolder(), "BruteWars.db");

        try {
            databaseFile.createNewFile();
        } catch (IOException e) {
            Logging.severe("Could not create the database.");
            e.printStackTrace();
        }

        // load SQLite database
        database = new SQLiteDatabase(plugin, databaseFile);

        /*
        Relationships:
        Players - Worlds: Many to many
        World - Owner: One to one
         */

        database.createTable("players",
                "`player_id` TEXT PRIMARY KEY");

        database.createTable("worlds",
                "`world_id` TEXT NOT NULL",
                "`owner_id` TEXT PRIMARY KEY",
                "`difficulty` TEXT NOT NULL",
                "'default_location' TEXT NOT NULL");

        database.createTable("members",
                "`world_id` TEXT NOT NULL",
                "`player_id` TEXT NOT NULL",
                "'last_location' TEXT NOT NULL");
    }

    // Runs synchronously on disable
    public void save() {
        database.backup();

        // create new database since we made a backup...
        createSQLiteDatabase();

        // insert data into tables
        bPlayerManager.getBPlayers().stream().map(BPlayer::getUuid).forEach(uuid -> database.getTable("players").insert(uuid.toString()));
        bWorldManager.getBWorlds().forEach(bWorld -> {
            final String bWorldUuid = bWorld.getUuid().toString();
            database.getTable("worlds").insert(bWorldUuid, bWorld.getOwner().getUuid().toString(), bWorld.getDifficulty().toString(), bWorld.getDefaultLocation().toString());
            bWorld.getPlayers(false).forEach(bPlayer -> database.getTable("members").insert(bWorldUuid, bPlayer.getUuid().toString(), bWorld.getLastLocation(bPlayer).toString()));
        });

        //close connection
        database.closeConnection();
    }

}