package de.exceptionflug.mccommons.plugin.proxy.player;

import de.exceptionflug.mccommons.inventories.api.PlayerWrapper;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProxyPlayerWrapper implements PlayerWrapper {

	private final ProxiedPlayer proxiedPlayer;

	public ProxyPlayerWrapper(final ProxiedPlayer proxiedPlayer) {
		this.proxiedPlayer = proxiedPlayer;
	}

	@Override
	public Object getHandle() {
		return proxiedPlayer;
	}

	@Override
	public int getProtocolVersion() {
		return ReflectionUtil.getProtocolVersion(proxiedPlayer);
	}
}
