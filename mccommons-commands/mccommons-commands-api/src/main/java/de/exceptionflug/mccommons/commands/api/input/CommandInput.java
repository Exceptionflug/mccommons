package de.exceptionflug.mccommons.commands.api.input;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;
import lombok.NonNull;

public class CommandInput {

    private final java.lang.String[] arguments;

    public final int getArgCount() {
        return arguments.length;
    }

    public CommandInput(final String... arguments) {
        this.arguments = arguments;
    }

    public <T> T get(final Class<T> clazz, final int index) {
        return InputSerializer.serialize(clazz, arguments[index]);
    }

    /**
     * @param argumentIndex Index to search our Integer in our arguments
     * @param errorMessage  Message that should be told to our players
     * @return
     */
    public final int parseInt(final int argumentIndex, @NonNull final java.lang.String... errorMessage) {
        Preconditions.checkArgument(argumentIndex > arguments.length - 1, "Can't parse Int: ArrayIndex would be out of bounds!");

        try {
            return Integer.parseInt(arguments[argumentIndex]);
        } catch (final NumberFormatException ex) {
            throw new CommandValidationException(errorMessage);
        }
    }

    /**
     * @param argumentIndex Index to search our Integer in our arguments
     * @param errorMessage  Message that should be told to our players
     * @return
     */
    public final double parseDouble(final int argumentIndex, @NonNull final java.lang.String... errorMessage) {
        Preconditions.checkArgument(argumentIndex > arguments.length - 1, "Can't parse Double: ArrayIndex would be out of bounds!");

        try {
            return Double.parseDouble(arguments[argumentIndex]);
        } catch (final NumberFormatException ex) {
            throw new CommandValidationException(errorMessage);
        }
    }
}
