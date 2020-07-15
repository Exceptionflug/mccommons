package de.exceptionflug.mccommons.plugin.spigot.converter;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.api.PlayerWrapper;
import de.exceptionflug.mccommons.plugin.spigot.player.SpigotPlayerWrapper;
import org.bukkit.entity.Player;

public class PlayerConverter implements Converter<Player, PlayerWrapper> {

	@Override
	public PlayerWrapper convert(final Player src) {
		return new SpigotPlayerWrapper(src);
	}

}
