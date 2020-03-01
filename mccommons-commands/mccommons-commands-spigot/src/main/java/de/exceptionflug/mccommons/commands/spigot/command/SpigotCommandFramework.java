package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mcccommons.commands.api.AbstractCommand;
import de.exceptionflug.mcccommons.commands.api.AbstractCommandFramework;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.NonNull;

public class SpigotCommandFramework extends AbstractCommandFramework {

    protected SpigotCommandFramework(final ConfigWrapper messageConfig) {
        super(messageConfig);
    }

    @Override
    public void registerCommand(final @NonNull AbstractCommand<?> command) {
        registeredCommands.add(command);

    }
}
