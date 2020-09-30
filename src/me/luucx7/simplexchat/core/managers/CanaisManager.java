package me.luucx7.simplexchat.core.managers;

import java.util.LinkedHashMap;

import org.bukkit.configuration.file.FileConfiguration;

import me.luucx7.simplexchat.SimplexChat;
import me.luucx7.simplexchat.cmds.Canais;
import me.luucx7.simplexchat.core.api.Channel;
import me.luucx7.simplexchat.core.model.channels.Canal;
import me.luucx7.simplexchat.core.model.channels.Local;

public class CanaisManager {
	
	public static LinkedHashMap<Channel, CommandManager> canaisCache = new LinkedHashMap<Channel, CommandManager>();
	public static Local local;
	
	private static FileConfiguration config = SimplexChat.cConfig;

	public static void load() {
		
		// Loads the local channel
		CommandManager localCmd = new CommandManager("l");
		localCmd.register(new Canais("l"));
		
		local = new Local();
		canaisCache.put(local, localCmd);
		
		// Loads all other channels
		config.getKeys(false).stream().filter(key -> !key.equalsIgnoreCase("local")).forEach(key -> {
			Channel canal = new Canal(key);
			
			CommandManager comando = new CommandManager(canal.getCommand());
			comando.register(new Canais(canal.getCommand()));
			
			canaisCache.put(canal, comando);
		});
	}
	
	public static void disable() {
		canaisCache.keySet().stream().forEach(canal -> {
			canaisCache.get(canal).unregister(canal.getCommand());
		});
	}
}
