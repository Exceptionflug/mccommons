package de.exceptionflug.mccommons.config.spigot;

import de.exceptionflug.mccommons.config.shared.RemoteConfigWrapper;
import org.bukkit.Location;

public interface SpigotConfig extends RemoteConfigWrapper {

	Location getLocation(final String path);

	SoundData getSoundData(final String path);

	PositionedSound getPositionedSound(final String path);

}
