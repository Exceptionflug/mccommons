package de.exceptionflug.mccommons.plugin.spigot.commands;

import de.exceptionflug.mccommons.holograms.Holograms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HologramReloadCommand implements CommandExecutor {
	@Override
	public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
		if (!commandSender.hasPermission("mccommons.command.hrl"))
			return false;
		Holograms.reload();
		commandSender.sendMessage("§8[§6MCCommons§8] §9Holograms reloaded.");
		return false;
	}
}
