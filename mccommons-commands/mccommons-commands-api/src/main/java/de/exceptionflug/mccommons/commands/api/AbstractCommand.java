package de.exceptionflug.mccommons.commands.api;


import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.commands.api.command.AbstractCommandSender;
import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import lombok.NonNull;

import java.util.Optional;

public abstract class AbstractCommand<S> {

    private AbstractCommandSender<S> commandSender;

    public abstract void onCommand(final CommandInput input);

    public final void setCommandSender(@NonNull final AbstractCommandSender<S> sender) {
        Preconditions.checkArgument(commandSender == null, "CommandSender is already set!");
        this.commandSender = sender;
    }

    protected final AbstractCommandSender<S> sender() {
        Preconditions.checkNotNull(commandSender, "CommandSender is not yet set!");
        return commandSender;
    }

    public Optional<String> getPermission() {
        return null;
    }

    protected void returnTell(final String... message) {
        throw new CommandValidationException(message);
    }

    protected void tell(final String... message) {
        //TODO
        commandSender.tell(message);
    }


}
