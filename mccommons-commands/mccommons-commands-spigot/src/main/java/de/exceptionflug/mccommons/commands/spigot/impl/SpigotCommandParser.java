package de.exceptionflug.mccommons.commands.spigot.impl;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.InGameOnly;
import de.exceptionflug.mccommons.commands.api.command.AbstractCommandParser;
import de.exceptionflug.mccommons.commands.api.command.CommandSettings;
import de.exceptionflug.mccommons.commands.api.command.MainCommand;
import de.exceptionflug.mccommons.commands.api.command.SubCommand;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommandHandler;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SpigotCommandParser extends AbstractCommandParser<SpigotCommandHandler> {

    public SpigotCommandParser(final AbstractCommand<?> mccCommand, final ConfigWrapper configWrapper) {
        super(mccCommand, configWrapper);
    }

    @Override
    public SpigotCommandHandler toCommand() {
        final SpigotCommandHandler.SpigotCommandHandlerBuilder builder = SpigotCommandHandler.builder();
        builder.mccCommand((SpigotCommand) mccCommand);
        builder.configWrapper(configWrapper);

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
        mainCommandBuilder.mccCommand(mccCommand);

        final Method mainCommandMethod = getMainCommandMethod();


        mainCommandBuilder.inGameOnly(mainCommandMethod.isAnnotationPresent(InGameOnly.class));

        final Optional<String> permission = getPermissionOfMethod(mainCommandMethod);
        mainCommandBuilder.permission(permission.orElse(""));

        mainCommandBuilder.maxArguments(maxArguments(mainCommandMethod));
        mainCommandBuilder.minArguments(minArguments(mainCommandMethod));

        builder.mainCommand(mainCommandBuilder.build());

        //        mainCommandBuilder.permission()

        //Subcommand

        final List<SubCommand> subCommands = new ArrayList<>();
        for (final Method method : getSubCommandMethods()) {
            final SubCommand.SubCommandBuilder subCommandBuilder = SubCommand.builder();

            final de.exceptionflug.mccommons.commands.api.annotation.SubCommand subCommand
                = method.getAnnotation(de.exceptionflug.mccommons.commands.api.annotation.SubCommand.class);

            subCommandBuilder.subCommand(method);
            subCommandBuilder.superCommand(mccCommand);
            subCommandBuilder.isInGameOnly(isInGameOnly(method));
            subCommandBuilder.minArguments(minArguments(method));
            subCommandBuilder.minArguments(maxArguments(method));
            subCommandBuilder.neededInput(subCommand.value());

            subCommands.add(subCommandBuilder.build());
        }
        builder.subCommands(subCommands);

        return builder.build();
    }
}
