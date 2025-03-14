package io.github.passengers.commands;

import io.github.passengers.managers.MessengerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ReplyCommand implements CommandExecutor {
    private MessengerManager messengerManager;

    public ReplyCommand(MessengerManager messengerManager) {
        this.messengerManager = messengerManager;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players can use this command."));
            return false;
        }

        Player playerMessageSender = (Player) commandSender;

        if (args.length < 1) {
            playerMessageSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInsufficient command syntax."));
            return false;
        }

        String playerMessageReceiverString = null;
        try {
            playerMessageReceiverString = messengerManager.getSender(playerMessageSender);
        } catch (SQLException e) {
            playerMessageSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSomething went wrong while communicating with the database, please contact the server administrator immediately. &7(005760)"));
            e.printStackTrace();
        }

        if (playerMessageReceiverString == null) {
            playerMessageSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou haven't received any messages to reply to."));
            return false;
        }

        Player playerMessageReceiver = Bukkit.getPlayer(playerMessageReceiverString);

        if (playerMessageReceiver == null) {
            playerMessageSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe player you tried to message isn't online anymore, we have sent them an email."));
        }

        String message = String.join(" ", args);

        try {
            messengerManager.sendMessage(playerMessageSender, playerMessageReceiver, message);
        } catch (SQLException e) {
            playerMessageSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSomething went wrong while communicating with the database, please contact the server administrator immediately. &7(108792)"));
            e.printStackTrace();
        }

        playerMessageSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "To " + playerMessageReceiverString + ": " + message));

        return true;
    }
}
