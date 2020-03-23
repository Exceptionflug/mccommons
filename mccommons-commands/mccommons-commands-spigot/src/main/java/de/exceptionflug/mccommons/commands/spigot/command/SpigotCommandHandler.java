package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.command.CommandSettings;
import de.exceptionflug.mccommons.commands.api.command.MainCommand;
import de.exceptionflug.mccommons.commands.api.command.SubCommand;
import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.impl.SpigotCommandSender;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.config.spigot.Message;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class SpigotCommandHandler extends org.bukkit.command.Command {
    private static final LocaleProvider localeProvider = Providers.get(LocaleProvider.class);

    private static final String NOT_FOR_CONSOLE = "[MCCommons] This command can't be run from console";

    private final List<SubCommand> subCommands;
    private final ConfigWrapper configWrapper;
    private final SpigotCommand mccCommand;
    private final MainCommand mainCommand;
    private final CommandSettings commandSettings;
    private CommandSender commandSender = null;
    private String[] args = new String[0];

    private SpigotCommandHandler(final CommandSettings commandSettings,
                                 final MainCommand mainCommand,
                                 final List<SubCommand> subCommands,
                                 final ConfigWrapper configWrapper,
                                 final SpigotCommand spigotCommand
    ) {
        //Will be changed
        super(null);

        final String[] names = commandSettings.getName();
        final int length = names.length;

        final List<String> aliases = length > 1 ? new ArrayList<>(Arrays.asList(names)).subList(1, length - 1) : new ArrayList<>();

        setLabel(names[0]);
        setName(names[0]);

        setAliases(aliases);

        this.commandSettings = commandSettings;
        this.mainCommand = mainCommand;
        this.subCommands = subCommands;
        this.configWrapper = configWrapper;
        this.mccCommand = spigotCommand;

    }

    public static SpigotCommandHandlerBuilder builder() {
        return new SpigotCommandHandlerBuilder();
    }

    @Override
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        this.commandSender = sender;
        this.args = args;

        mccCommand.setSender(new SpigotCommandSender(sender, configWrapper));
        mccCommand.setMsgConfig(configWrapper);

        final int length = args.length;

        try {

            //checking global rules

            checkConsole(commandSettings.isInGameOnly());

            checkPermission(commandSettings.getPermission().orElse(""));

            executeSubcommandIfPossible();


            //checking maincommand-rules

            checkConsole(mainCommand.isInGameOnly());

            final CommandInput input = new CommandInput(getLocale(), args);

            mccCommand.onCommand(input);

        } catch (final CommandValidationException ex) { //Command break-up condition
            if(ex.getMessages() == null) {
                tell(ex.getMessageKey(), ex.getDefaultMessage(), ex.getReplacements());
                return false;
            }
            if (ex.getMessages().length == 0) {
                return false;
            }
            tellPlain(ex.getMessages());
        } catch (final Throwable throwable) {
           mccCommand.handleException(throwable);
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
                returnTooFewArguments();
            }

            if (subCommandArguments.length > subCommand.getMaxArguments()) {
                //To many arguments
                returnTooManyArguments();
            }

            if (subCommand.isInGameOnly()) {
                returnConsole();
            }

            //Checking permission

            final String permission = subCommand.getPermission().orElse("");

            checkPermission(permission);

            subCommand.executeSubCommand(new CommandInput(getLocale(), subCommandArguments));

            //Break up the command-execution as our subcommand was found.
            throw new CommandValidationException();
        }
    }

    private Locale getLocale() {
        if (commandSender == null || !(commandSender instanceof Player)) {
            return Locale.GERMAN;
        }
        return localeProvider.provide(((Player) commandSender).getUniqueId());
    }

    private void checkConsole(final boolean condition) {
        if (!condition) {
            returnConsole();
        }
    }

    /**
     * Check the permission of the sender or break up the command.
     *
     * @param permission
     */
    private void checkPermission(final String permission) {
        if (permission == null || permission.isEmpty()) {
            return;
        }
        if (!commandSender.hasPermission(permission)) {
            returnNoPerm();
        }
    }


    private void returnTooFewArguments() {
        returnTell("Usage.TooFewArguments", "&cZu wenige Argumente.");
    }

    private void returnTooManyArguments() {
        returnTell("Usage.TooManyArguments", "&cZu viele Argumente.");
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
            returnTellPlain(NOT_FOR_CONSOLE);
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
    private void returnTellPlain(final String... message) {
        throw new CommandValidationException(message);
    }

    private void returnTell(final String messageKey, final String defaultMessage) {
        if (commandSender == null) {
            return;
        }
        Message.send(commandSender, configWrapper, messageKey, defaultMessage);
        throw new CommandValidationException();
    }

    private void tell(final String messageKey, final String defaultMessage, final String... replacements) {
        if (commandSender == null) {
            return;
        }
        Message.send(commandSender, configWrapper, messageKey, defaultMessage, replacements);
    }

    private void tellPlain(final String... messages) {
        for (final String message : messages) {
            commandSender.sendMessage(message);
        }
    }

    public static class SpigotCommandHandlerBuilder {
        private List<SubCommand> subCommands;
        private ConfigWrapper configWrapper;
        private SpigotCommand mccCommand;
        private MainCommand mainCommand;
        private CommandSettings commandSettings;
        private CommandSender commandSender;
        private String[] args;

        SpigotCommandHandlerBuilder() {
        }

        public SpigotCommandHandlerBuilder subCommands(final List<SubCommand> subCommands) {
            this.subCommands = subCommands;
            return this;
        }

        public SpigotCommandHandlerBuilder configWrapper(final ConfigWrapper configWrapper) {
            this.configWrapper = configWrapper;
            return this;
        }

        public SpigotCommandHandlerBuilder mccCommand(final SpigotCommand mccCommand) {
            this.mccCommand = mccCommand;
            return this;
        }

        public SpigotCommandHandlerBuilder mainCommand(final MainCommand mainCommand) {
            this.mainCommand = mainCommand;
            return this;
        }

        public SpigotCommandHandlerBuilder commandSettings(final CommandSettings commandSettings) {
            this.commandSettings = commandSettings;
            return this;
        }

        public SpigotCommandHandlerBuilder commandSender(final CommandSender commandSender) {
            this.commandSender = commandSender;
            return this;
        }

        public SpigotCommandHandlerBuilder args(final String[] args) {
            this.args = args;
            return this;
        }

        public SpigotCommandHandler build() {
            return new SpigotCommandHandler(commandSettings, mainCommand, subCommands, configWrapper, mccCommand);
        }

        @Override public String toString() {
            return "SpigotCommandHandler.SpigotCommandHandlerBuilder(subCommands=" + this.subCommands + ", configWrapper=" + this.configWrapper + ", mccCommand=" + this.mccCommand + ", mainCommand=" + this.mainCommand + ", commandSettings=" + this.commandSettings + ", commandSender=" + this.commandSender + ", args=" + Arrays.deepToString(this.args) + ")";
        }
    }
}

