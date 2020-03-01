package de.exceptionflug.mcccommons.commands.api.command;

import com.google.common.base.Preconditions;
import de.exceptionflug.mcccommons.commands.api.AbstractCommand;
import de.exceptionflug.mcccommons.commands.api.input.CommandInput;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;


public final class SubCommand {
    //Command the SubCommand is child of
    private final AbstractCommand<?> superCommand;
    //Argument which is needed to execute this command
    //Example /ban perma test -> Needed Argument: "test"
    private final String neededArgument;
    //Method which should be executed
    private final Method subCommand;

    private String permission;

    private boolean isInGameOnly;


    public SubCommand(final AbstractCommand<?> superCommand,
                      final String neededArgument,
                      final Method toExecute,
                      final boolean isInGameOnly,
                      @Nullable final String permission) {

        Preconditions.checkArgument(
            toExecute.getParameterCount() == 1,
            "Method to execute must take one parameter which is an CommandInput!"
        );

        this.superCommand = superCommand;
        this.neededArgument = neededArgument;
        this.subCommand = toExecute;

        this.permission = null;
        this.isInGameOnly = false;
    }

    public SubCommand(final AbstractCommand<?> superCommand, final String neededArgument, final Method toExecute) {
        this(superCommand, neededArgument, toExecute, false, null);
    }

    public void executeSubCommand(final CommandInput commandInput) {
        try {
            subCommand.invoke(superCommand, commandInput);
        } catch (IllegalAccessException | InvocationTargetException ex) {
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
