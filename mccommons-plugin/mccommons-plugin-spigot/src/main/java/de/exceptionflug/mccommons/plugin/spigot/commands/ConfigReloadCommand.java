package de.exceptionflug.mccommons.plugin.spigot.commands;

import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ConfigReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!sender.hasPermission("mccommons.command.crl"))
			return false;
		ConfigFactory.reloadAll();
		sender.sendMessage("§8[§6MCCommons§8] §5Configs reloaded.");
		return false;
	}
}
