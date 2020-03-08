package de.exceptionflug.mccommons.commands.api.command;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

@Getter
@Builder
@Data
public final class SubCommand {
    //Command the SubCommand is child of
    private final AbstractCommand<?> superCommand;
    //Argument which is needed to execute this command
    //Example /ban perma test -> Needed Argument: "test"
    private final String neededInput;
    //Method which should be executed
    private final Method subCommand;
    private final int minArguments;
    private final int maxArguments;
    private final String permission;
    private final boolean isInGameOnly;


    public boolean isSubCommandTrigger(final String argumentString) {
        return argumentString.startsWith(neededInput);
    }

    public boolean isSubCommandTrigger(final String[] arguments) {
        final String argumentString = String.join(" ", arguments).toLowerCase();

        return isSubCommandTrigger(argumentString);
    }

    public void executeSubCommand(final CommandInput commandInput) {
        try {
            subCommand.invoke(superCommand, commandInput);
        } catch (final IllegalAccessException | InvocationTargetException ex) {
            System.err.println("Exception whilst executing subcommand: " + getClass().getName());
            ex.printStackTrace();
        }
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    //Don't use lombok here -> IsPrefix
    public boolean isInGameOnly() {
        return isInGameOnly;
    }
}
