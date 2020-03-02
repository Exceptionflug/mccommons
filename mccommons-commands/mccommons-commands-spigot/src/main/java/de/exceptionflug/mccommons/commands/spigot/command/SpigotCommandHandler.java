package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.command.SubCommand;
import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.impl.SpigotCommandSender;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.config.spigot.Message;
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
    private String[] args;

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

    @Override
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        this.commandSender = sender;
        this.args = args;

        mccCommand.setCommandSender(new SpigotCommandSender(sender));

        final int length = args.length;

        try {

            // ----------------------------------------------------------------------------------------------------
            // No sub-command found. Give arguments to our main-command
            // ----------------------------------------------------------------------------------------------------

            executeSubcommandIfPossible();

            if (inGameOnly) {
                returnConsole();
            }

            final CommandInput input = new CommandInput(args);

            if (length < minArguments) {
                //To few arguments
                returnTell("Usage.TooFewArguments", "&cZu wenige Argumente");
            }

            if (length > maxArguments) {
                //To many arguments
                returnTell("Usage.TooFewMany", "&cZu viele Argumente");
            }

            checkPermission(permission.orElse(""));

            mccCommand.onCommand(input);

        } catch (final CommandValidationException ex) { //Command break-up condition
            if (ex.getMessages().length == 0) {
                return false;
            }
            tellPlain(ex.getMessages());
        } catch (final Throwable throwable) {
            tellPlain(
                "§cEs ist soeben ein Fehler beim Ausführen des Commands aufgetreten.",
                "§cBitte melde dies umgehend dem Team: ",
                " ",
                "§7Ausnahme: §e" + throwable.getClass().getName(),
                "§7Nachricht: §e" + throwable.getMessage()
            );
        }

        return false;
    }

    private void executeSubcommandIfPossible() {
        // /ban perma linksKeineMitte -> [perma, linksKeineMitte] -> "ban linksKeineMitte"
        final String joinedArguments = String.join(" ", args).toLowerCase();

        for (final SubCommand subCommand : subCommands) {
            if (!subCommand.isSubCommandTrigger(joinedArguments)) {
                continue; //Not the subcommand we search for
            }

            final String[] subCommandArguments = joinedArguments
                .replace(subCommand.getNeededInput(), "")
                .split(" ");

            if (subCommandArguments.length < subCommand.getMinArguments()) {
                //To many arguments

                returnTell("Usage.TooFewArguments", "Zu wenige Argumente");
            }

            if (subCommandArguments.length > subCommand.getMaxArguments()) {
                //To many arguments
                returnTell("Usage.TooManyArguments", "Zu viele Argumente");
            }

            if (subCommand.isInGameOnly()) {
                returnConsole();
            }

            //Checking permission

            final String permission = subCommand.getPermission().orElse(permission().orElse(""));

            checkPermission(permission);

            subCommand.executeSubCommand(new CommandInput(subCommandArguments));

            //Break up the command-execution as our subcommand was found.
            throw new CommandValidationException();
        }
    }

    /**
     * Check the permission of the sender or break up the command.
     *
     * @param permission
     */
    private void checkPermission(final String permission) {
        if (permission.isEmpty()) {
            return;
        }
        if (!commandSender.hasPermission(permission)) {
            returnNoPerm();
        }
    }

    /**
     * Breaks up the command
     * <p>
     * and tells the player
     * that this command can't be used in the console
     */
    private void returnConsole() {
        if (commandSender == null) {
            return;
        }

        if (!(commandSender instanceof Player)) {
            returnTell(NOT_FOR_CONSOLE);
        }
    }

    /**
     * Breaks up the command
     * <p>
     * and tells the player that this command requires a permission
     * the player doesn't have
     */
    private void returnNoPerm() {
        returnTell("NoPerm", "&cUnzureichende Berechtigungen, um diesen Command auszuführen");
    }

    /**
     * Breaks up the command which a message
     *
     * @param message Message to be send
     */
    private void returnTell(final String... message) {
        throw new CommandValidationException(message);
    }

    private void returnTell(String messageKey, final String defaultMessage) {
        if (commandSender == null) {
            return;
        }
        Message.send(commandSender, configWrapper, messageKey, defaultMessage);
        throw new CommandValidationException();
    }

    private void tell(final String messageKey, final String defaultMessage) {
        if (commandSender == null) {
            return;
        }
        Message.send(commandSender, configWrapper, messageKey, defaultMessage);
    }

    private void tellPlain(final String... messages) {
        commandSender.sendMessage(messages);
    }
}

