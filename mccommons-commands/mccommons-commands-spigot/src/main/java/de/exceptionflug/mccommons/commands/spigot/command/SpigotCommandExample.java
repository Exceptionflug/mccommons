package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.CommandArgs;
import de.exceptionflug.mccommons.commands.api.annotation.InGameOnly;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;

@Command(value = {"ban", "bannane", "bamburg"})
public class SpigotCommandExample extends SpigotCommand {

    private static String TEST = "TEST";

    @SubCommand("perma")
    @InGameOnly
    @CommandArgs(minArgsLength = 1, maxArgsLength = 2)
    public void test(final CommandInput input) {

    }

    @Override
    @InGameOnly
    public void onCommand(final CommandInput input) {
    }
}