package de.exceptionflug.mccommons.commands.spigot.impl;

import de.exceptionflug.mcccommons.commands.api.command.AbstractCommandSender;
import org.bukkit.command.CommandSender;

public final class SpigotCommandSender extends AbstractCommandSender<CommandSender> {

    public SpigotCommandSender(final CommandSender handle) {
        super(handle);
    }

    @Override
    public void tell(final String... message) {
        handle.sendMessage(message);
    }
}
