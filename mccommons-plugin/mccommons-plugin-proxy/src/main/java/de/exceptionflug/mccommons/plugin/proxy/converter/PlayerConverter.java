package de.exceptionflug.mccommons.plugin.proxy.converter;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.api.PlayerWrapper;
import de.exceptionflug.mccommons.plugin.proxy.player.ProxyPlayerWrapper;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerConverter implements Converter<ProxiedPlayer, PlayerWrapper> {

	@Override
	public PlayerWrapper convert(final ProxiedPlayer src) {
		return new ProxyPlayerWrapper(src);
	}

}
