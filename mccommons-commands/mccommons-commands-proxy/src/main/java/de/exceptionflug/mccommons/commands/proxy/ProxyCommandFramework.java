package de.exceptionflug.mccommons.commands.proxy;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.AbstractCommandFramework;
import de.exceptionflug.mccommons.commands.proxy.impl.ProxyCommandParser;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.NonNull;

public class ProxyCommandFramework extends AbstractCommandFramework {

    protected ProxyCommandFramework(final ConfigWrapper messageConfig) {
        super(messageConfig);
    }


    @Override
    public void registerCommand(@NonNull final AbstractCommand<?> command) {
        registeredCommands.add(command);
        Commands.registerCommand(new ProxyCommandParser(command).toCommand());
    }
}
