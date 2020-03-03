package de.exceptionflug.mccommons.commands.spigot.impl;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.InGameOnly;
import de.exceptionflug.mccommons.commands.api.command.AbstractCommandParser;
import de.exceptionflug.mccommons.commands.api.command.CommandSettings;
import de.exceptionflug.mccommons.commands.api.command.MainCommand;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommandHandler;

import java.lang.reflect.Method;

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

        //General stuff
        final Command command = getCommandAnnotation();

        final CommandSettings.CommandSettingsBuilder commandSettingsBuilder = CommandSettings.builder();

        commandSettingsBuilder.inGameOnly(command.inGameOnly());
        commandSettingsBuilder.permission(command.permission());
        commandSettingsBuilder.name(command.value());

        builder.commandSettings(commandSettingsBuilder.build());

        //Main command

        final MainCommand.MainCommandBuilder mainCommandBuilder = MainCommand.builder();
        final Method mainCommandMethod = getMainCommandMethod();


        mainCommandBuilder.inGameOnly(mainCommandMethod.isAnnotationPresent(InGameOnly.class));
        //        mainCommandBuilder.permission()

        //Subcommand

        for (final Method method : getSubCommandMethods()) {

        }

        return builder.build();
    }
}
