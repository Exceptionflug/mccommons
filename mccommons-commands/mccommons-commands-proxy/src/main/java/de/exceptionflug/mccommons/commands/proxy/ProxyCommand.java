package de.exceptionflug.mccommons.commands.proxy;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.config.proxy.Message;
import de.exceptionflug.mccommons.inventories.proxy.utils.Schedulable;
import net.md_5.bungee.api.CommandSender;

public abstract class ProxyCommand extends AbstractCommand<CommandSender> implements Schedulable {

	@Override
	protected void tell(String messageKey, String defaultMessage, String... replacements) {
		Message.send(getSender().handle, msgConfig, messageKey, defaultMessage, replacements);
	}
}
