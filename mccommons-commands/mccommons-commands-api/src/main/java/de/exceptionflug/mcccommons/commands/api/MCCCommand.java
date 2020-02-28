package de.exceptionflug.mcccommons.commands.api;


import com.google.common.base.Preconditions;
import de.exceptionflug.mcccommons.commands.api.command.CommandSender;
import de.exceptionflug.mcccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mcccommons.commands.api.input.CommandInput;
import lombok.NonNull;

import java.util.Optional;

public abstract class MCCCommand {

    private CommandSender commandSender;

    public final void commandSender(@NonNull final CommandSender sender) {
        Preconditions.checkArgument(commandSender == null, "CommandSender is already set!");
        this.commandSender = sender;
    }

    protected final CommandSender commandSender() {
        Preconditions.checkNotNull(commandSender, "CommandSender is not yet set!");
        return commandSender;
    }

    public abstract void onCommand(final CommandInput input);

    public Optional<String> getPermission() {
        return null;
    }

    protected void returnTell(String... message) {
        throw new CommandValidationException(message);
    }

    protected void tell(String... message) {
        //TODO
    }
}
