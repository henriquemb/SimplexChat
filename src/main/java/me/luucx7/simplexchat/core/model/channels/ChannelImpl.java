package me.luucx7.simplexchat.core.model.channels;

import lombok.Getter;
import lombok.Setter;
import me.luucx7.simplexchat.SimplexChat;
import me.luucx7.simplexchat.core.api.Channel;
import org.bukkit.configuration.file.FileConfiguration;

public class ChannelImpl implements Channel {

	@Getter
	final String name;
	@Getter
	private String command;
	@Getter @Setter
	String format;
	@Getter @Setter
	boolean broadcast;
	@Getter @Setter
	boolean restrict;
	boolean actionbar;
	@Getter @Setter
	String permission;
	@Getter @Setter
	int radius;
	
	public static FileConfiguration config = SimplexChat.cConfig;
	
	public ChannelImpl(String name) {
		this.name = name;
		
		load();
	}
	
	@Override
	public void load() {
		this.command = config.getString(name + ".command");
		this.broadcast = config.getBoolean(name + ".broadcast");
		this.restrict = config.getBoolean(name + ".restrict");
		this.radius = config.getInt(name + ".radius");
		this.permission = config.getString(name + ".permission");
		this.format = config.getString(name + ".format");
		this.actionbar = config.getBoolean(name + ".actionbar");
	}

	@Override
	public boolean useActionbar() {
		return this.actionbar;
	}

	@Override
	public void setActionbar(boolean actionbar) {
		this.actionbar = actionbar;
	}
}
