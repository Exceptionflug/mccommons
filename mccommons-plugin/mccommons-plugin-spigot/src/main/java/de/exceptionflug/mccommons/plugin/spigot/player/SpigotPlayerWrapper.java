package de.exceptionflug.mccommons.plugin.spigot.player;

import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.PlayerWrapper;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import org.bukkit.entity.Player;

public class SpigotPlayerWrapper implements PlayerWrapper {

	private final Player player;

	public SpigotPlayerWrapper(final Player player) {
		this.player = player;
	}

	@Override
	public Object getHandle() {
		return player;
	}

	@Override
	public int getProtocolVersion() {
		return Providers.get(ServerVersionProvider.class).getProtocolVersion();
	}
}
