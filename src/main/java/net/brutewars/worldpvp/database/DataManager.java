package net.brutewars.worldpvp.database;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.player.BPlayerManager;
import net.brutewars.worldpvp.rank.Rank;
import net.brutewars.worldpvp.rank.RankManager;
import net.brutewars.worldpvp.utils.Logging;
import net.brutewars.worldpvp.world.BWorld;
import net.brutewars.worldpvp.world.BWorldManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public final class DataManager {
    private final BWorldPlugin plugin;

    private final BPlayerManager bPlayerManager;
    private final BWorldManager bWorldManager;

    private SQLiteDatabase database;

    public DataManager(final BWorldPlugin plugin, final BPlayerManager bPlayerManager, final BWorldManager bWorldManager) {
        this.plugin = plugin;
        this.bPlayerManager = bPlayerManager;
        this.bWorldManager = bWorldManager;
    }

    // Runs synchronously on enable
    public void init() {
        createSQLiteDatabase();

        // load everything in the database
        // load all players first
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

        // load all BWorlds second
        database.getWorldsTable().select(worldSet -> {
            try {
                while (worldSet.next()) {
                    // get basic BWorld info
                    final UUID worldUuid = UUID.fromString(worldSet.getString("world_id"));
                    final BPlayer owner = bPlayerManager.getBPlayer(UUID.fromString(worldSet.getString("owner_id")));

                    final BWorld bWorld = bWorldManager.loadBWorld(worldUuid, owner, false);

                    // get BWorld members
                    database.getMembersTable().select("*", "`world_id` = \"" + worldUuid + "\"", memberSet -> {
                        try {
                            while (memberSet.next()) {
                                final UUID playerUuid = UUID.fromString(memberSet.getString("player_id"));
                                final Rank rank = RankManager.getRank(memberSet.getInt("player_rank"));
                                bWorld.addPlayer(bPlayerManager.getBPlayer(playerUuid), rank);
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

        //close connection
        database.closeConnection();
    }

    public void createSQLiteDatabase() {
        // we get the database file
        final File databaseFile = new File(plugin.getDataFolder(), "BruteWarsWorldPvP.db");

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
            final String bWorldUuid = bWorld.getWorldName();
            database.getWorldsTable().insert(bWorldUuid, bWorld.getOwner().getUuid().toString());
            bWorld.getPlayers(false).forEach(bPlayer -> database.getMembersTable().insert(bWorldUuid, bPlayer.getUuid().toString(), String.valueOf(bPlayer.getRank().getWeight())));
        });

        //close connection
        database.closeConnection();
    }

}