package net.brutewars.sandbox.database;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.lastlocation.LastLocation;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.player.BPlayerManager;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.bworld.BWorldManager;

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
    }

    // loading everything from the database
    // Runs synchronously on enable
    public void init() {
        createSQLiteDatabase();

        // first we load all players
        database.getPlayersTable().select(playerSet ->  {
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
        database.getWorldsTable().select(worldSet -> {
            try {
                while (worldSet.next()) {
                    // get basic BWorld info
                    final UUID worldUuid = UUID.fromString(worldSet.getString("world_id"));
                    final BPlayer owner = bPlayerManager.getBPlayer(UUID.fromString(worldSet.getString("owner_id")));
                    final BWorld bWorld = bWorldManager.loadBWorld(worldUuid, owner, false);

                    bWorld.setDefaultLocation(new LastLocation(worldSet.getString("last_location")));

                    // get BWorld members
                    database.getMembersTable().select("*", "`world_id` = \"" + worldUuid + "\"", memberSet -> {
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
    }

    // Runs synchronously on disable
    public void save() {
        database.backup();

        // create new database since we made a backup...
        createSQLiteDatabase();

        // insert data into tables
        bPlayerManager.getBPlayers().stream().map(BPlayer::getUuid).forEach(uuid -> database.getPlayersTable().insert(uuid.toString()));
        bWorldManager.getBWorlds().forEach(bWorld -> {
            /*
              BWorld#getWorldName() returns the uuid as a String
             */
            final String bWorldUuid = bWorld.getWorldName();
            database.getWorldsTable().insert(bWorldUuid, bWorld.getOwner().getUuid().toString(), bWorld.getDefaultLocation().toString());
            bWorld.getPlayers(false).forEach(bPlayer -> database.getMembersTable().insert(bWorldUuid, bPlayer.getUuid().toString(), bWorld.getLastLocation(bPlayer).toString()));
        });

        //close connection
        database.closeConnection();
    }

}