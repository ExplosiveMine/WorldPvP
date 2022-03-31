package net.brutewars.sandbox.database;

import lombok.Getter;
import lombok.SneakyThrows;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.utils.Logging;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public final class SQLiteDatabase {
    private final BWorldPlugin plugin;
    private final File databaseFile;

    private Connection connection;

    //tables
    @Getter private final Table playersTable;
    @Getter private final Table worldsTable;
    @Getter private final Table membersTable;

    public SQLiteDatabase(final BWorldPlugin plugin, final File databaseFile) {
        this.plugin = plugin;
        this.databaseFile = databaseFile;

        playersTable = createTable("players",
                "`player_id` TEXT PRIMARY KEY");

        worldsTable = createTable("worlds",
                "`world_id` TEXT PRIMARY KEY",
                "`owner_id` TEXT NOT NULL");

        membersTable = createTable("members",
                "`world_id` TEXT NOT NULL",
                "`player_id` TEXT NOT NULL",
                "`player_rank` INTEGER NOT NULL");

    }

    @SneakyThrows(SQLException.class)
    public Connection getConnection() {
        if (isConnected()) return connection;

        //load driver
        loadDriver();

        // cache
        connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        return connection;
    }


    private void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Logging.severe("You need the SQLite JDBC library in the /lib folder");
        }
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public void executeUpdate(String s) {
        try (final PreparedStatement preparedStatement = getConnection().prepareStatement(s)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeQuery(String s, Consumer<ResultSet> callback) {
        try (final PreparedStatement preparedStatement = getConnection().prepareStatement(s)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            callback.accept(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows(SQLException.class)
    public void closeConnection() {
        if (connection != null) connection.close();
    }

    public Table createTable(final String name, final String... fields) {
        final Table table = new Table(name);
        table.create(fields);
        return table;
    }

    @SneakyThrows
    public void backup() {
        final String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Logging.debug(plugin, "Backup: " + time);
        final Path path = Paths.get(plugin.getDataFolder() + File.separator + "backups");
        Files.createDirectories(path);
        Files.move(databaseFile.toPath(), Paths.get(path + File.separator + time.replaceAll("\\.", "-") + ".db"));
    }

    public final class Table {
        private final String name;

        public Table(final String name) {
            this.name = name;
        }

        public void create(final String... fields) {
            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + name + "` (");
            for (int i = 0; i < fields.length; i++) {
                sb.append(fields[i]);
                if (i == fields.length - 1)
                    sb.append(")");
                else
                    sb.append(",");
            }

            Logging.debug(plugin, "Database: " + sb);
            executeUpdate(sb.toString());
        }

        public void insert(final Object... values) {
            final StringBuilder sb = new StringBuilder("INSERT INTO `" + name + "` VALUES (\"");
            for (int i = 0; i < values.length; i++) {
                sb.append(values[i]).append("\"");
                if (i == values.length - 1) {
                    sb.append(")");
                } else {
                    sb.append(", \"");
                }
            }

            Logging.debug(plugin, "Database: " + sb);
            executeUpdate(sb.toString());
        }

        public void select(final Consumer<ResultSet> callback) {
            select("*", "", callback);
        }

        public void select(final String selection, final String condition, final Consumer<ResultSet> callback) {
            final StringBuilder sb = new StringBuilder("SELECT " + selection + " FROM `" + name + "`");
            if (!condition.isEmpty())
                sb.append(" WHERE ").append(condition);

            Logging.debug(plugin, "Database: " + sb);
            executeQuery(sb.toString(), callback);
        }

        /*
        public void delete(final String... conditions) {
            final StringBuilder sb = new StringBuilder("DELETE FROM `" + name + "`");
            if (conditions != null && conditions.length != 0)
                sb.append(" WHERE ").append(Arrays.toString(conditions));

            StringUtils.debug(plugin, "Database: " + sb);
            executeUpdate(sb.toString());
        }
        */

        /*
        public void update(final String set, final String condition) {
            Preconditions.checkArgument(condition != null && !condition.isEmpty(), "cannot update the whole database");

            final StringBuilder sb = new StringBuilder("UPDATE `" + name + "` SET " + set);
            sb.append(" WHERE ").append(condition);

            StringUtils.debug(plugin, "Database: " + sb);
            executeUpdate(sb.toString());
        }
        */

    }

}