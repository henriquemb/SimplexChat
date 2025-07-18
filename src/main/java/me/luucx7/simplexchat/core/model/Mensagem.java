package me.luucx7.simplexchat.core.model;

import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import me.luucx7.simplexchat.SimplexChat;
import me.luucx7.simplexchat.core.api.Channel;
import me.luucx7.simplexchat.core.managers.MessageManager;
import me.luucx7.simplexchat.core.nms.ActionBar;
import me.luucx7.simplexchat.core.utils.MessageSender;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class Mensagem {

	Player sender;
	String[] mensagem;
	String mensagemString;
	String consoleMsg;
	String mensagemFinal;
	Channel canal;
	int quantia;
	boolean isValid;

	public Mensagem(Player sender, String[] mensagem, Channel canal) {
		this.sender = sender;
		this.mensagem = mensagem;
		this.canal = canal;
		this.quantia = 0;
	}

	public Mensagem (Player sender, String message, Channel channel) {
		this(sender, message.split(" "), channel);
	}

	public Mensagem validar() {
		String msg = String.join(" ", mensagem);

		if (SimplexChat.instance.getConfig().getBoolean("modules.chatformat.spam.enable") && !sender.hasPermission("chat.chatformat.spam.bypass") && MessageManager.isSpam(mensagem)) {
			MessageSender.sendMessage(sender, SimplexChat.instance.getConfig().getString("modules.chatformat.spam.message"));
			return this;
		}

		if (SimplexChat.instance.getConfig().getBoolean("modules.chatformat.flood.enable") && !sender.hasPermission("chat.chatformat.flood.bypass")) {
			if (MessageManager.isFlood((Player) sender, msg)) {
				MessageSender.sendMessage(sender, SimplexChat.instance.getConfig().getString("modules.chatformat.flood.message"));

				return this;
			}
			else MessageManager.setLastMessage(sender, msg);
		}

		if (SimplexChat.instance.getConfig().getBoolean("modules.chatformat.lowercase") && !sender.hasPermission("chat.chatformat.lowercase.bypass")) {
			mensagem = MessageManager.formatLowerCase(mensagem);
		}

		if (SimplexChat.instance.getConfig().getBoolean("modules.chatformat.title_format")) {
			mensagem = MessageManager.formatTitle(mensagem);
		}

		this.isValid = true;
		return this;
	}

	public Mensagem preparar() {
		String formato = canal.getFormat();
		mensagemString = mensagem[0];
		if (mensagem.length>1) {
			for (int i = 1;i<mensagem.length;i++) {
				mensagemString = mensagemString + " "+ mensagem[i];
			}
		}

		assert sender != null;
		if (!sender.hasPermission("chat.colored")) {
			mensagemString = ChatColor.stripColor(mensagemString)
					.replace("show_entity=", "")
					.replace("show_item=", "")
					.replace("&", "")
					.replace("color", "");
		}

		if (SimplexChat.useFilter && !sender.hasPermission("chat.filter.bypass")) {

			SimplexChat.filterConfig.getStringList("remove").stream().forEach(s -> {
				mensagemString = mensagemString.replace(s, "");
			});

			SimplexChat.filterConfig.getStringList("replace").stream().forEach(s -> {
				int index = -1;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ':') {
						if (i > 0 && s.charAt(i - 1) == '\\') continue;

						index = i;
						break;
					}
				}

				if (index > 0 && s.charAt(index - 1) != '\\') {
					String toReplace = s.substring(0, index);
					String replacer = s.substring(index + 1);

					toReplace = toReplace.replace("\\:", ":");
					replacer = replacer.replace("\\:", ":");

					mensagemString = mensagemString.replace(toReplace, replacer);
				}
			});
		}

		formato = formato.replace("<message>", mensagemString);
		if (sender!=null) formato = formato.replace("<player>", sender.getName());

		String replacedMessage = PlaceholderAPI.setPlaceholders(sender, formato).replace("<br>", "\n");
		mensagemFinal = replacedMessage.trim().replaceAll(" +", " ");
		
		if (SimplexChat.instance.getConfig().getBoolean("log_to_console")) {
			consoleMsg = SimplexChat.instance.getConfig().getString("console_log");
			consoleMsg = consoleMsg.replace("<channel>", canal.getName())
			.replace("<channelCmd>", canal.getCommand())
			.replace("<player>", sender!=null ? sender.getName() : "Discord")
			.replace("<message>", mensagemString)
			;
			
			consoleMsg = PlaceholderAPI.setPlaceholders(sender, consoleMsg);
		}
		return this;
	}

	public void enviar() {
		if (!isValid) return;

		ArrayList<Player> recebedores = new ArrayList<Player>();
		
		if (canal.isBroadcast()) {
			Bukkit.getOnlinePlayers().stream().forEach(p -> {
				recebedores.add(p);
				if (!p.hasPermission("chat.bypasscount")) quantia++;
			});
		} else {
			int chanelRadius = canal.getRadius();
			Bukkit.getOnlinePlayers().stream().filter(p -> p.getLocation().getWorld().getName().equals(sender.getLocation().getWorld().getName())).filter(p -> p.getLocation().distance(sender.getLocation())<=chanelRadius).forEach(p -> {
				recebedores.add(p);
				if (!p.hasPermission("chat.bypasscount")) quantia++;
			});
		}
		
		if (canal.isRestrict()) {
			recebedores.stream().filter(r -> r.hasPermission(canal.getPermission())).forEach(r -> MessageSender.sendMessage(sender, r, mensagemFinal));
		} else {
			recebedores.stream().forEach(r -> MessageSender.sendMessage(sender, r, mensagemFinal));
		}
		
		if (canal.useActionbar()) {
			String actionMessage = quantia>1
					? SimplexChat.instance.getConfig().getString("amount_readed").replace("<amount>", (quantia-1)+"")
					: SimplexChat.instance.getConfig().getString("no_one");

			if (SimplexChat.getInstance().getServer().getVersion().contains("1.8")) {
				ActionBar.sendActionBar(sender, actionMessage);
			} else {
				MessageSender.sendActionBar(sender, actionMessage);
			}
		}
		if (SimplexChat.instance.getConfig().getBoolean("log_to_console")) {
			Bukkit.getConsoleSender().sendMessage(consoleMsg);
		}

		if (SimplexChat.isDiscordSRV()) {
			if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(canal.getName()) == null) return;
			if (StringUtils.isEmpty(mensagem.toString())) return;
			DiscordSRV.getPlugin().processChatMessage(sender, mensagemString, canal.getName(), false);
		}
	}
}
