package de.exceptionflug.mccommons.commands.api.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractCommandSender<S> {
    public final S handle;

    public abstract void tell(final String... messages);
}
