package io.github.passengers.commands;

import io.github.passengers.managers.MessengerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {
    private MessengerManager messengerManager;

    public MessageCommand(MessengerManager messengerManager) {
        this.messengerManager = messengerManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players can use this command."));
            return false;
        }

        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInsufficient command syntax."));
            return false;
        }

        Player playerMessageSender = (Player) commandSender;

        Player playerMessageReceiver = Bukkit.getPlayer(args[0]);

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }

        String message = messageBuilder.toString().trim();

        try {
            if (playerMessageReceiver != null && playerMessageReceiver.isOnline()) {
                messengerManager.sendMessage(playerMessageSender, playerMessageReceiver, message);
                playerMessageSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "To " + playerMessageReceiver.getName() + ": " + message));
                playerMessageReceiver.sendMessage(ChatColor.translateAlternateColorCodes('&', "From " + playerMessageSender.getName() + ": " + message));
                System.out.println("Successfully delivered the message.");
            } else {
                if (playerMessageReceiver == null) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer name cannot be null."));
                    return false;
                }
                messengerManager.sendMessage(playerMessageSender, playerMessageReceiver, message);
                System.out.println("Successfully stored message for later use.");
            }
        } catch (Exception e) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSomething went wrong while communicating with the database, please contact the administrators of this server immediately. &7(923488)"));
            e.printStackTrace();
        }

        return true;
    }
}