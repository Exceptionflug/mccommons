package de.exceptionflug.mccommons.commands.api;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommandFramework {
    protected final ConfigWrapper messageConfig;

    protected final List<AbstractCommand<?>> registeredCommands = new ArrayList<>();

    public abstract void registerCommand(@NonNull final AbstractCommand<?> command);

    public List<AbstractCommand<?>> getRegisteredCommands() {
        // We don't want anybody to modify our commands
        return Collections.unmodifiableList(registeredCommands);
    }
}
