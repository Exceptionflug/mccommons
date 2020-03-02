package de.exceptionflug.mccommons.commands.api.exception;

import lombok.NonNull;

/*+
Used to end a command.
 */
public final class CommandValidationException extends RuntimeException {
    private final String[] messages;

    public String[] getMessages() {
        return messages;
    }


    public CommandValidationException(@NonNull final String... message) {
        this.messages = message;
    }


}

