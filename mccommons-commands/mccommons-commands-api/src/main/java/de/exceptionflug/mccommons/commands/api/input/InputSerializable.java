package de.exceptionflug.mccommons.commands.api.input;

import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;

import java.util.Locale;

public abstract class InputSerializable<T> {

    /**
     * @return Class that should be serialized
     */
    public abstract Class<T> getClazz();

    /**
     * @param input        The input String our class should be serialized form
     * @param senderLocale Locale of the sender.
     *                     Should be uses to send a localized error-message
     *                     if the serialization fails
     */
    public abstract T serialize(final String input, final Locale senderLocale);


    protected void handleError(final Throwable throwable, final String input) {
        System.err.println("Exception while parsing " + getClazz().getName() + " from " + input);
        throwable.printStackTrace();
    }

    protected final void throwException(final String... reason) {
        throw new CommandValidationException(reason);
    }
}
