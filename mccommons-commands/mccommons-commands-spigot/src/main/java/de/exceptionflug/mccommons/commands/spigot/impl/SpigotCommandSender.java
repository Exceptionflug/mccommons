package de.exceptionflug.mccommons.commands.spigot.impl;

import de.exceptionflug.mccommons.commands.api.command.AbstractCommandSender;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.config.spigot.Message;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

public final class SpigotCommandSender extends AbstractCommandSender<CommandSender> {

    private final ConfigWrapper configWrapper;

    public SpigotCommandSender(final CommandSender handle, ConfigWrapper configWrapper) {
        super(handle);
        this.configWrapper = configWrapper;
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
    public void tellPlain(final String... message) {
        handle.sendMessage(message);
    }

    @Override
    public void tell(String msgKey, String defaultMessage, String... replacements) {
        Message.send(getHandle(), configWrapper, msgKey, defaultMessage, replacements);
    }

}
