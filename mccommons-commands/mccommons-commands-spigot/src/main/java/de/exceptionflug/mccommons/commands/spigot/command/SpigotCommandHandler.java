package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mcccommons.commands.api.command.SubCommand;
import de.exceptionflug.mcccommons.commands.api.exception.CommandValidationException;
import de.exceptionflug.mcccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.Builder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Builder
public final class SpigotCommandHandler extends org.bukkit.command.Command {

    private final List<SubCommand> subCommands;
    private final boolean inGameOnly;
    private final Optional<String> permission;
    private final ConfigWrapper configWrapper;
    private final SpigotCommand mccCommand;
    private CommandSender commandSender;

    private SpigotCommandHandler(final String name,
                                 final List<SubCommand> subCommands,
                                 final boolean inGameOnly,
                                 final ConfigWrapper configWrapper,
                                 final SpigotCommand spigotCommand,
                                 final @Nullable String permission
    ) {
        super(name);
        this.subCommands = subCommands;
        this.inGameOnly = inGameOnly;
        this.permission = Optional.ofNullable(permission);
        this.configWrapper = configWrapper;
        this.mccCommand = spigotCommand;
    }

    public Optional<String> permission() {
        return permission;
    }

    @Override public boolean execute(final CommandSender sender, final String label, final String[] args) {

        this.commandSender = sender;

        if (inGameOnly && (!(sender instanceof Player))) {
            tell("[MCCommons] This command can't be run from console");
        }

        final int length = args.length;

        try {
            if (length == 0) {
                mccCommand.onCommand(new CommandInput(args));
            }


        } catch (final CommandValidationException ex) {
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

