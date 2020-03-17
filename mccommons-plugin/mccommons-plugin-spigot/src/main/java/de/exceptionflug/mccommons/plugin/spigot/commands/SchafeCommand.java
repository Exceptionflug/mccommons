package de.exceptionflug.mccommons.plugin.spigot.commands;

import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;

@Command(value = "schafe", inGameOnly = true)
public final class SchafeCommand extends SpigotCommand {

    @Override
    public void onCommand(final CommandInput input) {
        System.out.println("Schafe züchten");
        tell("Schafe züchten macht spaß");
    }
}
