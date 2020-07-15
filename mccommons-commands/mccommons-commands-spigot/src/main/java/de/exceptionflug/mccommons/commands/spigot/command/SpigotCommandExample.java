package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.CommandArgs;
import de.exceptionflug.mccommons.commands.api.annotation.InGameOnly;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;

@Command(value = {"ban", "bannane", "bamburg"})
public final class SpigotCommandExample extends SpigotCommand {

	private static final String TEST = "TEST";

	@InGameOnly
	@SubCommand(value = "perma")
	@CommandArgs(minArgsLength = 1, maxArgsLength = 2)
	public void test(final CommandInput input) {

	}

	@Override
	@InGameOnly
	public void onCommand(final CommandInput input) {
	}
}