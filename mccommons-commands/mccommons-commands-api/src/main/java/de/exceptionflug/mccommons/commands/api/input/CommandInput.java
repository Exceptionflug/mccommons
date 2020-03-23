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

    public String getString(final int index) {
        return arguments[index];
    }

    public String findString(final int index, @NonNull final String errorMsgKey, @NonNull final String errorDefaultMsg, @NonNull final String... replacements) {
        if (arguments.length > index + 1) {
            throw new CommandValidationException(errorMsgKey, errorDefaultMsg, replacements);
        }
        return arguments[index];
    }


    public final int getArgCount() {
        return arguments.length;
    }

    public <T> T find(final Class<T> clazz, final int index) {
        return InputSerializer.serialize(clazz, arguments[index], senderLocale);
    }

    /**
     * @param argumentIndex   Index to search our Integer in our arguments
     * @param errorMsgKey     the message key of the localized message
     * @param errorDefaultMsg the default message of the localized message
     * @param replacements    replacements for placeholders
     * @return the found int
     */
    public final int findInt(final int argumentIndex, @NonNull final String errorMsgKey, @NonNull final String errorDefaultMsg, @NonNull final String... replacements) {
        try {
            return Integer.parseInt(arguments[argumentIndex]);
        } catch (final NumberFormatException ex) {
            throw new CommandValidationException(errorMsgKey, errorDefaultMsg, replacements);
        }
    }

    /**
     * @param argumentIndex   Index to search our Integer in our arguments
     * @param errorMsgKey     the message key of the localized message
     * @param errorDefaultMsg the default message of the localized message
     * @param replacements    replacements for placeholders
     * @return
     */
    public final double findDouble(final int argumentIndex, @NonNull final String errorMsgKey, @NonNull final String errorDefaultMsg, @NonNull final String... replacements) {
        Preconditions.checkArgument(argumentIndex > arguments.length - 1, "Can't parse Double: ArrayIndex would be out of bounds!");

        try {
            return Double.parseDouble(arguments[argumentIndex]);
        } catch (final NumberFormatException ex) {
            throw new CommandValidationException(errorMsgKey, errorDefaultMsg, replacements);
        }
    }
}
