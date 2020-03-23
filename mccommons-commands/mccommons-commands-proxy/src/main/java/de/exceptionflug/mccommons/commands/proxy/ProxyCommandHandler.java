package de.exceptionflug.mccommons.commands.proxy;

import de.exceptionflug.mccommons.commands.api.command.CommandSettings;
import de.exceptionflug.mccommons.commands.api.command.MainCommand;
import de.exceptionflug.mccommons.commands.api.command.SubCommand;
import de.exceptionflug.mccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.proxy.impl.ProxyCommandSender;
import de.exceptionflug.mccommons.config.proxy.Message;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProxyCommandHandler extends Command {

    private static final String NOT_FOR_CONSOLE = "[MCCommons] This command can't be run from console";

    private final List<SubCommand> subCommands;
    private final ConfigWrapper configWrapper;
    private final ProxyCommand mccCommand;
    private final MainCommand mainCommand;
    private final CommandSettings commandSettings;
    private CommandSender commandSender;
    private String[] args;

    private ProxyCommandHandler(final CommandSettings commandSettings,
                                final MainCommand mainCommand,
                                final List<SubCommand> subCommands,
                                final ConfigWrapper configWrapper,
                                final ProxyCommand proxyCommand
    ) {
        //Will be changed
        super(commandSettings.getName()[0], null, aliases(commandSettings.getName()));

        this.commandSettings = commandSettings;
        this.mainCommand = mainCommand;
        this.subCommands = subCommands;
        this.configWrapper = configWrapper;
        this.mccCommand = proxyCommand;
    }

    private static String[] aliases(final String[] name) {
        if (name.length > 1) {
            final String[] out = new String[name.length - 1];
            System.arraycopy(name, 1, out, 0, out.length);
            return out;
        }
        return new String[0];
    }

    public static ProxyCommandHandlerBuilder builder() {
        return new ProxyCommandHandlerBuilder();
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        this.commandSender = sender;
        this.args = args;

        mccCommand.setSender(new ProxyCommandSender(sender, configWrapper));
        mccCommand.setMsgConfig(configWrapper);

        final int length = args.length;

        try {

            //checking global rules

            checkConsole(commandSettings.isInGameOnly());

            checkPermission(commandSettings.getPermission().orElse(""));

            executeSubcommandIfPossible();


            //checking maincommand-rules

            if (mainCommand.getMinArguments() != -1 && length < mainCommand.getMinArguments()) {
                //To many arguments
                returnTooFewArguments();
            }

            if (mainCommand.getMaxArguments() != -1 && length > mainCommand.getMaxArguments()) {
                //To many arguments
                returnTooManyArguments();
            }


            checkConsole(mainCommand.isInGameOnly());

            final CommandInput input = new CommandInput(getLocale(), args);

            mccCommand.onCommand(input);

        } catch (final CommandValidationException ex) { //Command break-up condition
            if (ex.getMessages() == null) {
                tell(ex.getMessageKey(), ex.getDefaultMessage(), ex.getReplacements());
                return;
            }
            if (ex.getMessages().length == 0) {
                return;
            }
            tellPlain(ex.getMessages());
        } catch (final Throwable throwable) {
            mccCommand.handleException(throwable);
        }

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

            if (subCommand.getMinArguments() != -1 && subCommandArguments.length < subCommand.getMinArguments()) {
                //To many arguments
                returnTooFewArguments();
            }

            if (subCommand.getMaxArguments() != -1 && subCommandArguments.length > subCommand.getMaxArguments()) {
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

    public Locale getLocale() {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return Locale.GERMAN;
        }
        final UUID uuid = ((ProxiedPlayer) commandSender).getUniqueId();
        return Providers.get(LocaleProvider.class).provide(uuid);
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
        returnTell("Usage.TooFewArguments", "§cZu wenige Argumente.");
    }

    private void returnTooManyArguments() {
        returnTell("Usage.TooManyArguments", "§cZu viele Argumente.");
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

        if (!(commandSender instanceof ProxiedPlayer)) {
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

    public static class ProxyCommandHandlerBuilder {
        private List<SubCommand> subCommands;
        private ConfigWrapper configWrapper;
        private ProxyCommand mccCommand;
        private MainCommand mainCommand;
        private CommandSettings commandSettings;
        private CommandSender commandSender;
        private String[] args;

        ProxyCommandHandlerBuilder() {
        }

        public ProxyCommandHandlerBuilder subCommands(final List<SubCommand> subCommands) {
            this.subCommands = subCommands;
            return this;
        }

        public ProxyCommandHandlerBuilder configWrapper(final ConfigWrapper configWrapper) {
            this.configWrapper = configWrapper;
            return this;
        }

        public ProxyCommandHandlerBuilder mccCommand(final ProxyCommand mccCommand) {
            this.mccCommand = mccCommand;
            return this;
        }

        public ProxyCommandHandlerBuilder mainCommand(final MainCommand mainCommand) {
            this.mainCommand = mainCommand;
            return this;
        }

        public ProxyCommandHandlerBuilder commandSettings(final CommandSettings commandSettings) {
            this.commandSettings = commandSettings;
            return this;
        }

        public ProxyCommandHandler build() {
            return new ProxyCommandHandler(commandSettings, mainCommand, subCommands, configWrapper, mccCommand);
        }

        @Override public String toString() {
            return "ProxyCommandHandler.ProxyCommandHandlerBuilder(subCommands=" + this.subCommands + ", configWrapper=" + this.configWrapper + ", mccCommand=" + this.mccCommand + ", mainCommand=" + this.mainCommand + ", commandSettings=" + this.commandSettings + ", commandSender=" + this.commandSender + ", args=" + Arrays.deepToString(this.args) + ")";
        }
    }
}
