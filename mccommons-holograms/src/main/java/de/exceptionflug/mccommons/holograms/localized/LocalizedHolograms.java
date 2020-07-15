package de.exceptionflug.mccommons.holograms.localized;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.holograms.Holograms;

public class LocalizedHolograms {

	public static LocalizedHologramBuilder createBuilder(final ConfigWrapper messageConfig) {
		return new LocalizedHologramBuilder(Holograms.getRegistrant(), messageConfig);
	}

}
