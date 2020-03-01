package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mcccommons.commands.api.annotation.Command;
import de.exceptionflug.mcccommons.commands.api.annotation.CommandArgs;
import de.exceptionflug.mcccommons.commands.api.annotation.InGameOnly;
import de.exceptionflug.mcccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mcccommons.commands.api.input.CommandInput;

@InGameOnly
@Command(value = {"ban", "bannane", "bamburg"})
public class SpigotCommandExample extends SpigotCommand {

    private static String TEST = "TEST";

    @SubCommand("perma")
    @InGameOnly
    @CommandArgs(minArgsLength = 1, maxArgsLength = 2)
    public void test(final CommandInput input) {
    }

    @Override
    public void onCommand(final CommandInput input) {

    }
}