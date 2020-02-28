package de.exceptionflug.mcccommons.commands.api;

import de.exceptionflug.mcccommons.commands.api.annotation.Command;
import de.exceptionflug.mcccommons.commands.api.annotation.CommandArgs;
import de.exceptionflug.mcccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mcccommons.commands.api.input.CommandInput;

@Command(value = {"ban", "bannane", "bamburg"}, inGameOnly = true)
public class TestCommand {


	@SubCommand("perma")
	@CommandArgs(minArgsLength = 1, maxArgsLength = 2)
	public void test(final CommandInput input) {
	}
}
