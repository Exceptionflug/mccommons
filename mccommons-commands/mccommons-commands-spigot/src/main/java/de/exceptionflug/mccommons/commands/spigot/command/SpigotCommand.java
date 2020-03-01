package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mcccommons.commands.api.exception.CommandValidationException;
import lombok.Builder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

@Builder
public final class SpigotCommand extends org.bukkit.command.Command {

    private final Map<String, Method> subCommands;
    private final boolean inGameOnly;
    private final String permission;

    public SpigotCommand(final String name, final Map<String, Method> subCommands, final boolean inGameOnly, final @Nullable String permission) {
        super(name);
        this.subCommands = subCommands;
        this.inGameOnly = inGameOnly;
        this.permission = permission;
    }

    public Optional<String> permission() {
        return Optional.ofNullable(permission);
    }

    @Override public boolean execute(final CommandSender commandSender, final String s, final String[] strings) {

	    if (inGameOnly && (!(commandSender instanceof Player))) {
	    }




	    try {

	    } catch (final CommandValidationException ex) {



	    } catch (final Throwable throwable) {


	    }

        return false;
    }
}

