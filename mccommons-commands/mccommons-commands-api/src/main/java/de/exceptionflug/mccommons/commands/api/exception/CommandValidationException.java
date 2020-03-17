package de.exceptionflug.mccommons.commands.api.exception;

import lombok.NonNull;


/**
 * Used to end a command
 * <p>
 * Message will be told to the player.
 */
public final class CommandValidationException extends RuntimeException {
    private final String[] messages;

    public CommandValidationException(@NonNull final String... message) {
        this.messages = message;
    }

    public String[] getMessages() {
        return messages;
    }


}

