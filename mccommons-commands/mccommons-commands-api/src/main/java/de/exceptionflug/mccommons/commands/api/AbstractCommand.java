package de.exceptionflug.mccommons.commands.api;


import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.commands.api.command.AbstractCommandSender;
import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.NonNull;

import java.util.Optional;

public abstract class AbstractCommand<S> {

	private AbstractCommandSender<S> commandSender;
	protected ConfigWrapper msgConfig;

	public abstract void onCommand(final CommandInput input);

	protected final AbstractCommandSender<S> getSender() {
		Preconditions.checkNotNull(commandSender, "CommandSender is not yet set!");
		return commandSender;
	}

	public final void setSender(@NonNull final AbstractCommandSender<S> sender) {
		Preconditions.checkNotNull(sender, "CommandSender is can't be null!");
		this.commandSender = sender;
	}

	public void setMsgConfig(ConfigWrapper msgConfig) {
		this.msgConfig = msgConfig;
	}

	public Optional<String> getPermission() {
		return Optional.empty();
	}

	public void handleException(final Throwable throwable) {
		tellPlain(
			"§cEs ist soeben ein Fehler beim Ausführen des Commands aufgetreten.",
			"§cBitte melde dies umgehend dem Team: ",
			" ",
			"§7Ausnahme: §e" + throwable.getClass().getName(),
			"§7Nachricht: §e" + throwable.getMessage()
		);
	}

	protected void returnTellPlain(final String... message) {
		throw new CommandValidationException(message);
	}

	protected abstract void tell(String messageKey, String defaultMessage, String... replacements);

	protected void tellPlain(final String... message) {
		commandSender.tellPlain(message);
	}
}
