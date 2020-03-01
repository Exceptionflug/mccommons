package de.exceptionflug.mccommons.commands.spigot.impl;

import de.exceptionflug.mcccommons.commands.api.AbstractCommand;
import de.exceptionflug.mcccommons.commands.api.command.AbstractCommandParser;
import org.bukkit.command.Command;

public final class SpigotCommandParser extends AbstractCommandParser<Command> {

    public SpigotCommandParser(final AbstractCommand<?> mccCommand) {
        super(mccCommand);
    }

    @Override
    public org.bukkit.command.Command toCommand() {
        return null;
    }
}
