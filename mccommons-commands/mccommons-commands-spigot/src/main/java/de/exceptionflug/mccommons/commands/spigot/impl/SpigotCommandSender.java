package de.exceptionflug.mccommons.commands.spigot.impl;

import de.exceptionflug.mccommons.commands.api.command.AbstractCommandSender;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

public final class SpigotCommandSender extends AbstractCommandSender<CommandSender> {

    public SpigotCommandSender(final CommandSender handle) {
        super(handle);
    }

    @Override
    public Locale getLocale() {
        if (!(handle instanceof Player)) {
            return Locale.GERMAN;
        }
        final UUID uuid = ((Player) handle).getUniqueId();
        return Providers.get(LocaleProvider.class).provide(uuid);
    }

    @Override
    public void tell(final String... message) {
        handle.sendMessage(message);
    }
}
