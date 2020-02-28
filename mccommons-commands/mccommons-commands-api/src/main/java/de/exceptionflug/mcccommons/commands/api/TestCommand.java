package de.exceptionflug.mcccommons.commands.api;

import de.exceptionflug.mcccommons.commands.api.annotation.Command;
import de.exceptionflug.mcccommons.commands.api.annotation.CommandArgs;
import de.exceptionflug.mcccommons.commands.api.annotation.InGameOnly;
import de.exceptionflug.mcccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mcccommons.commands.api.input.CommandInput;

@InGameOnly
@Command(value = {"ban", "bannane", "bamburg"})
public class TestCommand implements de.exceptionflug.mcccommons.commands.api.Command {

	private static String Test = "TEST";


	@Override public void onCommand(final CommandInput input) {

	}

	@SubCommand("perma")
	@InGameOnly
	@CommandArgs(minArgsLength = 1, maxArgsLength = 2)
	public void test(final CommandInput input) {
	}


}
