package de.exceptionflug.mccommons.commands.api.command;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
/**
 * Class for settings of the Main-Command (onCommand)
 * method of our AbstractCommand.
 *
 * Defines things like
 * - whether the command should be executed ingame-only
 * - How many arguments the commands needs atleast/at max
 * - Which permission the command needs
 */
public final class MainCommand {
	private final AbstractCommand<?> mccCommand;
	private final boolean inGameOnly;
	private final String permission;
	private final int minArguments;
	private final int maxArguments;

	public Optional<String> getPermission() {
		return Optional.ofNullable(permission);
	}

	public void execute(final CommandInput commandInput) {
		mccCommand.onCommand(commandInput);
	}
}
