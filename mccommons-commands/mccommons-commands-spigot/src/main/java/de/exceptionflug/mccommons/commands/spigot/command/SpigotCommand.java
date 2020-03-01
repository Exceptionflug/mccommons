package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mcccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.inventories.spigot.utils.Schedulable;
import org.bukkit.command.CommandSender;

public abstract class SpigotCommand extends AbstractCommand<CommandSender> implements Schedulable {
}
