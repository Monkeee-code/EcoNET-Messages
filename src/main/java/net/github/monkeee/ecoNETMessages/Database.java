package net.github.monkeee.ecoNETMessages;

import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class Database {

    private final EcoNETMessages plugin;
    private Connection connection;

    public Database(EcoNETMessages plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "storage.db");
            plugin.getDataFolder().mkdirs();

            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            plugin.getLogger().info("Database connected!");
            createTables();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to database: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection has been closed");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not close the database connection: " + e.getMessage());
        }
    }

    public void createTables() {
        String table1 = """
                CREATE TABLE IF NOT EXISTS toggles (
                    uuid TEXT PRIMARY KEY,
                    enabled INTEGER DEFAULT 1
                )""";
        String table2 = """
                CREATE TABLE IF NOT EXISTS blocks (
                    uuid TEXT,
                    player TEXT,
                    blocked INTEGER DEFAULT 0,
                    PRIMARY KEY (uuid, player)
                )""";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(table1);
            stmt.execute(table2);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create tables: " + e.getMessage());
        }
    }

    public void createPlayerToggles(Player player) {
        String togglesCreate = "INSERT OR IGNORE INTO toggles (uuid, enabled) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(togglesCreate)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setInt(2, 1);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setToggle(Player player, boolean toggle) {
        String sql = "UPDATE toggles SET enabled = ? WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, toggle ? 1 : 0);
            stmt.setString(2, player.getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getToggle(Player player) {
        String sql = "SELECT enabled FROM toggles WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("enabled") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     *
     * @param sender Player, who is being blocked
     * @param receiver Player, who is blocking the `sender`
     * @param blocked Sets the blocked status
     */
    public void setBlocked(Player sender, Player receiver, boolean blocked) {
        String sql = "INSERT OR REPLACE INTO blocks (uuid, player, blocked) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sender.getUniqueId().toString());
            stmt.setString(2, receiver.getUniqueId().toString());
            stmt.setInt(3, blocked ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param sender The one, who is being checked
     * @param receiver The one, who has `sender` blocked
     * @return Returns `true`, if `sender` is blocked, otherwise `false`
     */
    public boolean isBlocked(UUID sender, UUID receiver) {
        String sql = "SELECT blocked FROM blocks WHERE uuid = ? AND player = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sender.toString());
            stmt.setString(2, receiver.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("blocked") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
