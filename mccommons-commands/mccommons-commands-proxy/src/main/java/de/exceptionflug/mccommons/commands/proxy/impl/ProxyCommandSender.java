package de.exceptionflug.mccommons.commands.proxy.impl;

import de.exceptionflug.mccommons.commands.api.command.AbstractCommandSender;
import net.md_5.bungee.api.CommandSender;

public class ProxyCommandSender extends AbstractCommandSender<CommandSender> {
    private final CommandSender handle;

    public ProxyCommandSender(final CommandSender handle) {
        super(handle);
        this.handle = handle;
    }


    @Override
    public void tell(final String... messages) {
        for (final String message : messages) {
            handle.sendMessage(message);
        }
    }
}
