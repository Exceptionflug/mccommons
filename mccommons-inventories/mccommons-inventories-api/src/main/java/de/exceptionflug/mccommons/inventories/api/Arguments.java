package de.exceptionflug.mccommons.inventories.api;

import java.util.ArrayList;
import java.util.List;

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
