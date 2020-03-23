package de.exceptionflug.mccommons.plugin.spigot.commands;

import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;

@Command("test")
public final class ExampleCommand extends SpigotCommand {

    @Override public void onCommand(final CommandInput input) {
        tellPlain("Heyyy:)");
    }

    @SubCommand("arena")
    public final void testArena(final CommandInput input) {
        tellPlain("Heyyy:) Arena");
    }
}
