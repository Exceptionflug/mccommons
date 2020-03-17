package de.exceptionflug.mccommons.commands.api.input;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;
import lombok.NonNull;

import java.util.Locale;

public final class CommandInput {

    private final Locale senderLocale;
    private final java.lang.String[] arguments;

    public CommandInput(final Locale senderLocale, final String... arguments) {
        this.senderLocale = senderLocale;
        this.arguments = arguments;
    }

    public final int getArgCount() {
        return arguments.length;
    }

    public <T> T find(final Class<T> clazz, final int index) {
        return InputSerializer.serialize(clazz, arguments[index], senderLocale);
    }

    /**
     * @param argumentIndex Index to search our Integer in our arguments
     * @param errorMessage  Message that should be told to our players
     * @return
     */
    public final int findInt(final int argumentIndex, @NonNull final java.lang.String... errorMessage) {
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
    public final double findDouble(final int argumentIndex, @NonNull final java.lang.String... errorMessage) {
        Preconditions.checkArgument(argumentIndex > arguments.length - 1, "Can't parse Double: ArrayIndex would be out of bounds!");

        try {
            return Double.parseDouble(arguments[argumentIndex]);
        } catch (final NumberFormatException ex) {
            throw new CommandValidationException(errorMessage);
        }
    }
}
