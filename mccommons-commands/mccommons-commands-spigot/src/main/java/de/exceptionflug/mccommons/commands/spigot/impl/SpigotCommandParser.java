package de.exceptionflug.mccommons.commands.spigot.impl;

import de.exceptionflug.mcccommons.commands.api.AbstractCommand;
import de.exceptionflug.mcccommons.commands.api.command.AbstractCommandParser;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommandHandler;
import org.bukkit.command.Command;

public final class SpigotCommandParser extends AbstractCommandParser<Command> {

    public SpigotCommandParser(final AbstractCommand<?> mccCommand) {
        super(mccCommand);
    }

    @Override
    public Command toCommand() {
        final SpigotCommandHandler.SpigotCommandHandlerBuilder builder = SpigotCommandHandler.builder();

        //Parsing the command


        return builder.build();
    }
}
