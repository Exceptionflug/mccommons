package de.exceptionflug.mcccommons.commands.api.input;

public interface InputSerializable<T> {

    Class<T> getClazz();

    T serialize(final String input);

}
