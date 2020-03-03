package de.exceptionflug.mccommons.commands.api.input;

import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;

public abstract class InputSerializable<T> {

    public abstract Class<T> getClazz();

    protected abstract T serialize(final String input);

    protected void handleError(final Throwable throwable, final String input) {
        System.err.println("Exception while parsing " + getClazz().getName() + " from " + input);
        throwable.printStackTrace();
    }

    protected final void throwException(final String... reason) {
        throw new CommandValidationException(reason);
    }
}
