package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mcccommons.commands.api.command.SubCommand;
import de.exceptionflug.mcccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mccommons.commands.spigot.impl.SpigotCommandSender;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.Builder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Builder
public final class SpigotCommandHandler extends org.bukkit.command.Command {

    private static final String NOT_FOR_CONSOLE = "[MCCommons] This command can't be run from console";

    private final List<SubCommand> subCommands;
    private final boolean inGameOnly;
    private final int minArguments;
    private final int maxArguments;
    private final Optional<String> permission;
    private final ConfigWrapper configWrapper;
    private final SpigotCommand mccCommand;
    private CommandSender commandSender;

    private SpigotCommandHandler(final String name,
                                 final List<SubCommand> subCommands,
                                 final boolean inGameOnly,
                                 final ConfigWrapper configWrapper,
                                 final SpigotCommand spigotCommand,
                                 final int minArguments, //For the main command
                                 final int maxArguments, //For the main command
                                 final @Nullable String permission
    ) {
        super(name);
        this.subCommands = subCommands;
        this.inGameOnly = inGameOnly;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.permission = Optional.ofNullable(permission);
        this.configWrapper = configWrapper;
        this.mccCommand = spigotCommand;
    }

    public Optional<String> permission() {
        return permission;
    }

    @Override public boolean execute(final CommandSender sender, final String label, final String[] args) {

        this.commandSender = sender;

        mccCommand.setCommandSender(new SpigotCommandSender(sender));

        final int length = args.length;

        try {

            // /ban perma linksKeineMitte -> [perma, linksKeineMitte] -> "ban linksKeineMitte"
            final String joinedArguments = String.join(" ", args).toLowerCase();

            for (final SubCommand subCommand : subCommands) {
                if (!joinedArguments.startsWith(subCommand.getNeededArgument())) {
                    continue; //Not the subcommand we search for
                }

                final String[] subCommandArguments = joinedArguments
                    .replace(subCommand.getNeededArgument(), "")
                    .split(" ");

                if (subCommandArguments.length < subCommand.getMinArguments()) {
                    //To few arguments
                }

                if (subCommandArguments.length > subCommand.getMaxArguments()) {
                    //To many arguments
                }

                if (subCommand.isInGameOnly() && !(sender instanceof Player)) {
                    tell(NOT_FOR_CONSOLE);
                }



            }


            // ----------------------------------------------------------------------------------------------------
            // No sub-command found. Give arguments to our main-command
            // ----------------------------------------------------------------------------------------------------

            if (inGameOnly && (!(sender instanceof Player))) {
                tell(NOT_FOR_CONSOLE);
            }

            //




        } catch (final CommandValidationException ex) { //Command break-up condition
            tell(ex.getMessages());
        } catch (final Throwable throwable) {
            tell(
                "§cEs ist soeben ein Fehler beim Ausführen des Commands aufgetreten.",
                "§cBitte melde dies umgehend dem Team: ",
                " ",
                "§7Ausnahme: §e" + throwable.getClass().getName(),
                "§7Nachricht: §e" + throwable.getMessage()
            );
        }

        return false;
    }

    private void tell(final String... messages) {
        commandSender.sendMessage(messages);
    }
}

