package me.luucx7.simplexchat.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.luucx7.simplexchat.SimplexChat;
import me.luucx7.simplexchat.core.api.ChatPlayer;
import me.luucx7.simplexchat.core.managers.JogadorManager;
import me.luucx7.simplexchat.core.utils.MessageSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cores implements CommandExecutor {
	    
    public static final Pattern RGB = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
    public static final Pattern LEGACY = Pattern.compile("&([0-9a-f]{1})");
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String arg, String[] args) {
		if (!(s instanceof Player)) {
			MessageSender.sendMessage(s, getString("only_players"));
			return true;
		}
		if (!s.hasPermission("chat.setcolor")) {
			MessageSender.sendMessage(s, getString("no_permission"));
			return true;
		}
		if (args.length==0) {
			MessageSender.sendMessage(s, getString("no_arg"));
			return true;
		}
		Player player = (Player) s;
		ChatPlayer jog = JogadorManager.get(player);
		
        if (args[0].equalsIgnoreCase("limpar") || args[0].equalsIgnoreCase("clear")) {
            jog.setColor("");
            MessageSender.sendMessage(s, getString("removed"));
            return true;
        }

        if(!validate(args[0])) {
        	MessageSender.sendMessage(s, getString("invalid"));
        	return true;
        }
        jog.setColor(args[0]);
        MessageSender.sendMessage(s, PlaceholderAPI.setPlaceholders((Player) s, getString("success")));
		return false;
	}

	private boolean validate(String hexColor) {
        Matcher rgbMatcher = RGB.matcher(hexColor);
        Matcher legacyMatcher = LEGACY.matcher(hexColor);
        
        if (SimplexChat.colorsConfig.getBoolean("enable_rgb") && rgbMatcher.matches()) {
        	return true;
        }
        return legacyMatcher.matches();
    }

	private static String getString(String path) {
		return SimplexChat.colorsConfig.getString(path);
	}

}