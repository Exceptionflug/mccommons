package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.AbstractCommandFramework;
import de.exceptionflug.mccommons.commands.spigot.impl.SpigotCommandParser;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.NonNull;

public class SpigotCommandFramework extends AbstractCommandFramework {


    private static volatile SpigotCommandFramework instance;

    public SpigotCommandFramework(final ConfigWrapper messageConfig) {
        super(messageConfig);
    }

    @Override
    public void registerCommand(@NonNull final AbstractCommand<?> command) {
        registeredCommands.add(command);
        Commands.registerCommand(new SpigotCommandParser(command).toCommand());
    }

    public void registerDefaultInputSerializables() {



    }


}
