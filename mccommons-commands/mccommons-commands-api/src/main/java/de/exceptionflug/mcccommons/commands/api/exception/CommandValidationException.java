package de.exceptionflug.mcccommons.commands.api.exception;

import lombok.NonNull;

/*+
Used to end a command.
 */
public final class CommandValidationException extends RuntimeException {
	private final String[] message;


	public CommandValidationException(@NonNull final String... message) {
		this.message = message;
	}


}

