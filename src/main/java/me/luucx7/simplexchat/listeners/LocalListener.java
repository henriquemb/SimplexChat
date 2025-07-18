package me.luucx7.simplexchat.listeners;

import me.luucx7.simplexchat.SimplexChat;
import me.luucx7.simplexchat.core.api.Channel;
import me.luucx7.simplexchat.core.managers.ChannelsManager;
import me.luucx7.simplexchat.core.managers.JogadorManager;
import me.luucx7.simplexchat.core.model.Mensagem;
import me.luucx7.simplexchat.core.utils.MessageSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LocalListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent ev) {
		if (ev.isCancelled()) {
			return;
		}
		Channel canal = ChannelsManager.local;
		
		if (!ChannelsManager.local.isEnabled()) {
			return;
		}
		ev.setCancelled(true);

		if (SimplexChat.instance.getConfig().getBoolean("modules.focus")) {
			canal = JogadorManager.get(ev.getPlayer()).getChannel();
		}
		if (canal.isRestrict() && !ev.getPlayer().hasPermission(canal.getPermission())) {
			MessageSender.sendMessage(ev.getPlayer(), SimplexChat.instance.getConfig().getString("no_permission"));
			return;
		}

		Mensagem msg = new Mensagem(ev.getPlayer(), ev.getMessage(), canal);
		msg.validar().preparar().enviar();
	}
}
