package io.github.passengers;

import io.github.passengers.commands.MessageCommand;
import io.github.passengers.commands.ReplyCommand;
import io.github.passengers.managers.MessengerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class OfflineMessenger extends JavaPlugin {
    private MessengerManager messengerManager;
    @Override
    public void onEnable() {
        System.out.println("You are currently running this plugin on " + getVersion());
        registerCommands();
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            messengerManager = new MessengerManager(getDataFolder().getAbsolutePath() + "/messages.db");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Unable to connect to the database.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        System.out.println("Successfully enabled the plugin.");
    }

    private void registerCommands() {
        getCommand("demomsg").setExecutor(new MessageCommand(messengerManager));
        getCommand("demoreply").setExecutor(new ReplyCommand(messengerManager));
    }
    @Override
    public void onDisable() {
        try {
            messengerManager.closeConnection();
            System.out.println("Successfully closed database communication.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully disabled the plugin.");
    }

    public MessengerManager getMessengerManager() {
        return messengerManager;
    }

    private static String getVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        String[] parts = version.split("\\.");
        return parts[3];
    }
}