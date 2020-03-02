package de.exceptionflug.mccommons.commands.spigot.impl;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.command.AbstractCommandParser;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommandHandler;

public final class SpigotCommandParser extends AbstractCommandParser<SpigotCommandHandler> {

    public SpigotCommandParser(final AbstractCommand<?> mccCommand) {
        super(mccCommand);
    }

    @Override
    public SpigotCommandHandler toCommand() {
        final SpigotCommandHandler.SpigotCommandHandlerBuilder builder = SpigotCommandHandler.builder();

        //Parsing the command

        Preconditions.checkArgument(
            isAnnotationPresent(Command.class),
            "Can not register command: Annotation @Command is not present"
        );


        final de.exceptionflug.mccommons.commands.api.annotation.Command command =
            mccClazz.getAnnotation(de.exceptionflug.mccommons.commands.api.annotation.Command.class);


        return builder.build();
    }
}
