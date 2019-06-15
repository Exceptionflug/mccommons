package de.exceptionflug.mccommons.inventories.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper object that wraps a {@link List} of {@link Object}s. This is used to easily access given arguments for a {@link ActionHandler} call.
 */
public class Arguments {

    private final List<Object> arguments;

    private Arguments() {
        arguments = new ArrayList<>();
    }

    public Arguments(final List<Object> arguments) {
        this.arguments = arguments;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public <T> T get(final int index) {
        return (T) arguments.get(index);
    }

    public static Arguments empty() {
        return new Arguments();
    }

}
