package de.exceptionflug.mccommons.holograms.localized;

import org.bukkit.entity.Player;

public interface PlayerboundReplacementSupplier {

	String[] get(final Player player);

}
