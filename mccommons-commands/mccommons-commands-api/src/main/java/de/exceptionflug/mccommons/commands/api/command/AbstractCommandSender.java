package de.exceptionflug.mccommons.commands.api.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public abstract class AbstractCommandSender<S> {
    public final S handle;

    public abstract Locale getLocale();

    public abstract void tell(final String... messages);
}
