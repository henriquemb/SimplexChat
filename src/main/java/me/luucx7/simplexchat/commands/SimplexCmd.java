package me.luucx7.simplexchat.commands;

import me.luucx7.simplexchat.core.utils.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SimplexCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command c, String arg, String[] args) {
		if (args.length==0) {
			MessageSender.sendMessage(s, "&b&lSimplexChat &r&b"+Bukkit.getServer().getPluginManager().getPlugin("SimplexChat").getDescription().getVersion()+" by Luucx7 @ SagaciusDev");
			return true;
		}
		return false;
	}
}
