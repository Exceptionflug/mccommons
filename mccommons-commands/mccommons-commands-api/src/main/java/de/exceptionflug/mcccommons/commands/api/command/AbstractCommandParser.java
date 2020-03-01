package de.exceptionflug.mcccommons.commands.api.command;

import de.exceptionflug.mcccommons.commands.api.AbstractCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractCommandParser<C> {
    private final AbstractCommand<?> mccCommand;

    public abstract C toCommand();
}
