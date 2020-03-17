package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.config.spigot.Message;
import de.exceptionflug.mccommons.inventories.spigot.utils.Schedulable;
import org.bukkit.command.CommandSender;

public abstract class SpigotCommand extends AbstractCommand<CommandSender> implements Schedulable {

    @Override
    protected void tell(String messageKey, String defaultMessage, String... replacements) {
        Message.send(getSender().handle, msgConfig, messageKey, defaultMessage, replacements);
    }
}
