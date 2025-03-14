package io.github.passengers.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;

public class MessengerManager {
    private final Connection connection;

    public MessengerManager(String path) throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().warning("Unable to detect className org.sqlite.JDBC");
            Bukkit.getServer().shutdown();
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS player_messages (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL, " +
                    "message TEXT, " +
                    "previous_message_player_UUID TEXT, " +
                    "previous_message_time DATETIME)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_messages (uuid, username) VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player_messages WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void sendMessage(Player senderPlayer, Player receiverPlayer, String message) throws SQLException {
        if (!playerExists(senderPlayer)) {
            addPlayer(senderPlayer);
            System.out.println("Successfully added " + senderPlayer.getName() + " to the database.");
        }
        if (!playerExists(receiverPlayer)) {
            addPlayer(receiverPlayer);
            System.out.println("Successfully added " + receiverPlayer.getName() + " to the database.");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE player_messages SET message = ?, previous_message_player_UUID = ?, previous_message_time = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, message);
            preparedStatement.setString(2, senderPlayer.getUniqueId().toString());
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(4, receiverPlayer.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public String getSender(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT previous_message_player_UUID FROM player_messages WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("previous_message_player_UUID");
            }
        }
        return null;
    }
}