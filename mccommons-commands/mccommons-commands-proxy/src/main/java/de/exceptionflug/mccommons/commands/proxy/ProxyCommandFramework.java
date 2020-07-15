package de.exceptionflug.mccommons.commands.proxy;

import de.exceptionflug.mccommons.commands.api.AbstractCommand;
import de.exceptionflug.mccommons.commands.api.AbstractCommandFramework;
import de.exceptionflug.mccommons.commands.api.input.InputSerializable;
import de.exceptionflug.mccommons.commands.api.input.InputSerializer;
import de.exceptionflug.mccommons.commands.proxy.impl.ProxyCommandParser;
import de.exceptionflug.mccommons.config.proxy.Message;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.UUID;

public final class ProxyCommandFramework extends AbstractCommandFramework {

	protected ProxyCommandFramework(final ConfigWrapper messageConfig) {
		super(messageConfig);
	}


	@Override
	public void registerCommand(@NonNull final AbstractCommand<?> command) {
		registeredCommands.add(command);
		Commands.registerCommand(new ProxyCommandParser(command, messageConfig).toCommand());
	}

	private void registerDefaultInputSerializables() {

		InputSerializer.registerSerializable(new InputSerializable<ProxiedPlayer>() {
			@Override
			public Class<ProxiedPlayer> getClazz() {
				return ProxiedPlayer.class;
			}

			@Override
			public ProxiedPlayer serialize(final String input, final Locale locale) {
				final ProxiedPlayer player;
				if (input.length() == 36) {
					final UUID targetUUID = UUID.fromString(input);
					player = ProxyServer.getInstance().getPlayer(targetUUID);
				} else {
					player = ProxyServer.getInstance().getPlayer(input);
				}

				if (player == null) {
					throwException(Message.getMessage(messageConfig, locale, "PlayerNotOnline", "&cDieser Spieler ist nicht online."));
				}
				return player;
			}
		});
	}
}
