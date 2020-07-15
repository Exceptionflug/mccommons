package de.exceptionflug.mccommons.commands.proxy.impl;

import de.exceptionflug.mccommons.commands.api.command.AbstractCommandSender;
import de.exceptionflug.mccommons.config.proxy.Message;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.UUID;

public class ProxyCommandSender extends AbstractCommandSender<CommandSender> {
	private final CommandSender handle;
	private final ConfigWrapper configWrapper;

	public ProxyCommandSender(final CommandSender handle, ConfigWrapper configWrapper) {
		super(handle);
		this.handle = handle;
		this.configWrapper = configWrapper;
	}

	@Override
	public Locale getLocale() {
		if (!(handle instanceof ProxiedPlayer)) {
			return Locale.GERMAN;
		}
		final UUID uuid = ((ProxiedPlayer) handle).getUniqueId();
		return Providers.get(LocaleProvider.class).provide(uuid);
	}

	@Override
	public void tell(String msgKey, String defaultMessage, String... replacements) {
		Message.send(getHandle(), configWrapper, msgKey, defaultMessage, replacements);
	}

	@Override
	public void tellPlain(final String... messages) {
		for (final String message : messages) {
			handle.sendMessage(message);
		}
	}
}
