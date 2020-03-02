package de.exceptionflug.mccommons.commands.api.input;

import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;

public abstract class InputSerializable<T> {

    public abstract Class<T> getClazz();

    public T serialize(final String input) {
        try {
            return serialize0(input);
        } catch (final CommandValidationException ex) {
            throw ex;
        } catch (final Throwable throwable) {
            handleError(throwable, input);
        }
        return null;
    }

    protected void handleError(final Throwable throwable, final String input) {
        System.err.println("Exception while parsing " + getClazz().getName() + " from " + input);
        throwable.printStackTrace();
    }

    protected abstract T serialize0(final String input) throws Throwable;
}
