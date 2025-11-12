package com.uni.gameclient.game.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManipulation {
    //private static final String JDBC_URL = "jdbc:h2:file:./data/mydb";
    private static final String JDBC_URL = "jdbc:h2:tcp://localhost:9092/./data/mydb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        initDatabase();
    }

    // === Datenbank initialisieren ===
    private static void initDatabase() {
       /* try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Tabelle anlegen
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS GAMESERVERS (
                    id VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(255),
                    uri VARCHAR(255),
                    max_players INT,
                    current_player_count INT,
                    status VARCHAR(50),
                    last_seen TIMESTAMP
                )
            """);

            // prüfen ob leer
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM gameservers")) {
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    insertDemoData(conn);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    // === Beispiel-Daten hinzufügen ===
    public void insertOrUpdateData(List<Gameserver> gameserver) throws SQLException {

        List<Gameserver> my_old_gamserver_list = getAllServers();

        for (Gameserver gs : my_old_gamserver_list) {

            String deleteSql = "DELETE FROM gameservers WHERE name = ?";
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setString(1, gs.getName());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        for (Gameserver gs : gameserver) {
            String insertSql = "INSERT INTO gameservers (id, name, uri, max_players, current_player_count, status, last_seen, server_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, gs.getName());
                ps.setString(3, gs.getUri());
                ps.setInt(4, gs.getMaxPlayers());
                ps.setInt(5, gs.getCurrentPlayerCount());
                ps.setString(6, gs.getStatus());
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBoolean(8, true);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}

    // === Alle Server abrufen ===
    public static List<Gameserver> getAllServers() {
        List<Gameserver> result = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM gameservers ORDER BY name")) {

            while (rs.next()) {
                Gameserver s = new Gameserver(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("uri"),
                        rs.getInt("max_players"),
                        rs.getInt("current_player_count"),
                        rs.getString("status"),
                        rs.getTimestamp("last_seen").toLocalDateTime(),
                        rs.getBoolean("server_Active")
                );
                result.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // === Alle Server abrufen ===
    public static Gameserver getServerWithName(String serverName) {
        Gameserver result = new Gameserver();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM gameservers Where name ='" + serverName+"'")) {
             Gameserver s = new Gameserver(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("uri"),
                        rs.getInt("max_players"),
                        rs.getInt("current_player_count"),
                        rs.getString("status"),
                        rs.getTimestamp("last_seen").toLocalDateTime(),
                        rs.getBoolean("server_active")
                );
             result=s;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // === Alle Server abrufen ===
    public static Gameserver getServerByUri(String serverUri) {
        Gameserver result = new Gameserver();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();

             ResultSet rs = stmt.executeQuery("SELECT * FROM gameservers Where URI ='" + serverUri+"' LIMIT 1")) {
            if (rs.next()) {
                Gameserver s = new Gameserver(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("uri"),
                        rs.getInt("max_players"),
                        rs.getInt("current_player_count"),
                        rs.getString("status"),
                        rs.getTimestamp("last_seen").toLocalDateTime(),
                        rs.getBoolean("server_active")
                );
                result = s;
            };

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
