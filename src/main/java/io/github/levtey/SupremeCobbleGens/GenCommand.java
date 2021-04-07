package io.github.levtey.SupremeCobbleGens;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class GenCommand implements CommandExecutor, TabCompleter {
	
	private SupremeCobbleGens plugin;
	
	public GenCommand(SupremeCobbleGens plugin) {
		this.plugin = plugin;
		plugin.getCommand("scg").setExecutor(this);
		plugin.getCommand("scg").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (!sender.hasPermission("scg.gui") || !(sender instanceof Player)) return true;
			new GenMenu(plugin, (Player) sender);
			return true;
		}
		if (!sender.hasPermission("scg.admin")) return true;
		if (args[0].equalsIgnoreCase("reload")) {
			plugin.saveDefaultConfig();
			plugin.reloadConfig();
			return true;
		} else if (args[0].equalsIgnoreCase("set")) {
			if (args.length < 3) return true;
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
			plugin.setTier(player, args[2]);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender.hasPermission("scg.admin")) {
			return plugin.getConfig().getConfigurationSection("generators").getKeys(false).stream().collect(Collectors.toList());
		}
		return null;
	}

}
