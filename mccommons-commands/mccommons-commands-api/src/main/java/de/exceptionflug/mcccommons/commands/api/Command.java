package de.exceptionflug.mcccommons.commands.api;


import de.exceptionflug.mcccommons.commands.api.command.CommandSender;
import de.exceptionflug.mcccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mcccommons.commands.api.input.CommandInput;

import java.util.Optional;

public interface Command {

	CommandSender getCommandSender();

	void onCommand(final CommandInput input);

	default Optional<String> getPermission() {
		return null;
	}

	default void returnTell(String... message) {
		throw new CommandValidationException(message);
	}

	default void tell(String... message) {
		//TODO
	}
}
