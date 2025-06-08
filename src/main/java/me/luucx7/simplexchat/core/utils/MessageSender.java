package me.luucx7.simplexchat.core.utils;

import com.github.henriquemb.papermessagemanager.manager.MessageManager;
import org.bukkit.command.CommandSender;

public class MessageSender {
    private static final MessageManager messageManager = new MessageManager("*");

    public static void sendMessage(CommandSender player, CommandSender target, String message) {
        messageManager.sendMessage(player, target, message);
    }

    public static void sendMessage(CommandSender target, String message) {
        messageManager.sendMessage(target, message);
    }

    public static void sendActionBar(CommandSender player, CommandSender target, String message) {
        messageManager.sendActionBar(player, target, message);
    }

    public static void sendActionBar(CommandSender target, String message) {
        messageManager.sendActionBar(target, message);
    }
}