package de.exceptionflug.mccommons.commands.api.exception;

import lombok.NonNull;


/**
 * Used to end a command
 * <p>
 * Message will be told to the player.
 */
public final class CommandValidationException extends RuntimeException {

	private final String[] messages;

	private final String messageKey;
	private final String defaultMessage;
	private final String[] replacements;

	public CommandValidationException(@NonNull final String... message) {
		super(null, null, true, false);
		this.messages = message;
		this.messageKey = null;
		this.defaultMessage = null;
		this.replacements = null;
	}

	public CommandValidationException(String messageKey, String defaultMessage, String... replacements) {
		super(null, null, true, false);
		this.messageKey = messageKey;
		this.defaultMessage = defaultMessage;
		this.replacements = replacements;

		this.messages = null;
	}

	public String[] getMessages() {
		return messages;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public String[] getReplacements() {
		return replacements;
	}
}

