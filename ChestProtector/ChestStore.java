package net.fricktastic.chestprotector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChestStore
{
    static String connectionString = "";

    private static Connection con = null;

    static Connection getConnection() throws SQLException
    {
        if (con == null) {
            con = DriverManager.getConnection(connectionString);
        }
        return con;
    }

    static void close()
    {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void migrate() throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS chests (" +
                "location VARCHAR(50)," +
                "owner VARCHAR(40)," +
                "private BOOLEAN DEFAULT true," +
                "allowed_players VARCHAR(370) DEFAULT ''," +
                "allowed_group VARCHAR(10) DEFAULT ''," +
                "flags VARCHAR(1) DEFAULT '0'," +
                "UNIQUE(location) )");
    }

    static void changeChestPrivacy(String location, boolean isPrivate) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET private=" + isPrivate + " WHERE location='" + location + "';");
    }

    static void updateOwner(String location, String owner) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET owner='" + owner + "' WHERE location='" + location + "';");
    }

    static void updateAllowedPlayers(String location, String allowedPlayers) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET allowed_players='" + allowedPlayers + ",' WHERE location='" + location + "';");
    }

    static void allowPlayer(String location, String player) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET allowed_players=allowed_players || '" + player + "' || ',' WHERE location='" + location + "';");
    }

    static void allowGroup(String group, String location) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET allowed_group='" + group + "' WHERE location='" + location + "';");
    }

    static void disallowGroup(String location) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET allowed_group='' WHERE location='" + location + "';");
    }

    public static void handleDisbandGroup(String group) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET allowed_group='' WHERE allowed_group='" + group + "';");
    }

    static void addPrivateChest(String location, String player) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("INSERT INTO chests ( location, owner ) VALUES ( '" + location + "', '" + player + "' );");
    }

    static void addPrivateChest(String location, String player, boolean isPrivate, String allowedPlayers) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("INSERT INTO chests ( location, owner, private, allowed_players ) VALUES ( '" + location + "', '" + player + "', '" + (isPrivate ? 1 : 0) + "', '" + allowedPlayers + "' );");
    }

    static void removePrivateChest(String location) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("DELETE FROM chests WHERE location='" + location + "';");
    }

    static boolean chestExists(String location) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT count(1) FROM chests WHERE location='" + location + "';");
        if (rs.next()) {
            return rs.getInt(1) == 1;
        }
        return false;
    }

    static void setFlag(String location) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET flags='1' WHERE location='" + location + "';");
    }

    static void unsetFlag(String location) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("UPDATE chests SET flags='0' WHERE location='" + location + "';");
    }

    static List<Object> getChestData(String location) throws SQLException
    {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT owner, private, allowed_players, allowed_group, flags FROM chests WHERE location='" + location + "';");
        if (rs.next()) {
            List<Object> result = new ArrayList<>();
            result.add(UUID.fromString(rs.getString("owner")));
            result.add(rs.getBoolean("private"));
            List<UUID> players = new ArrayList<>();
            for (String uuid : rs.getString("allowed_players").split(",")) {
                if (uuid.equals("")) continue;

                players.add(UUID.fromString(uuid));
            }
            result.add(players);
            result.add(Integer.parseInt(rs.getString("flags")));
            result.add(rs.getString("allowed_group"));

            return result;
        }
        return null;
    }
}
