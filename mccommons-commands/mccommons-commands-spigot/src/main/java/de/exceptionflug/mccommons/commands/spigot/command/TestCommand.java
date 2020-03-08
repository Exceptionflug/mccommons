package de.exceptionflug.mccommons.commands.spigot.command;

import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.InGameOnly;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;

@Command(value = "schafe", permission = "Schafe.Züchten", inGameOnly = true)
public final class TestCommand extends SpigotCommand {

    @Override
    @InGameOnly
    public void onCommand(final CommandInput input) {

    }


    @SubCommand(value = "more")
    public void onMore(final CommandInput input) {

    }
}
