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

    protected final AbstractCommandSender<S> getSender() {
        Preconditions.checkNotNull(commandSender, "CommandSender is not yet set!");
        return commandSender;
    }

    public final void setSender(@NonNull final AbstractCommandSender<S> sender) {
        Preconditions.checkNotNull(sender, "CommandSender is can't be null!");
        this.commandSender = sender;
    }

    public Optional<String> getPermission() {
        return Optional.empty();
    }

    protected void returnTell(final String... message) {
        throw new CommandValidationException(message);
    }

    protected void tell(final String... message) {
        //TODO
        commandSender.tell(message);
    }
}
