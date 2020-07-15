package de.exceptionflug.mccommons.config.spigot;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PositionedSound extends SoundData {

	private Location location;

	public PositionedSound(final Location location, final Sound sound, final float volume, final float pitch) {
		super(sound, volume, pitch);
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(final Location location) {
		this.location = location;
	}

	public void play() {
		location.getWorld().playSound(location, getSound(), getVolume(), getPitch());
	}

	@Override
	public void play(final Player p) {
		p.playSound(location, getSound(), getVolume(), getPitch());
	}
}
