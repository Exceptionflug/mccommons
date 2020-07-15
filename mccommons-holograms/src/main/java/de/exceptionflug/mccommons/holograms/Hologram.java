package de.exceptionflug.mccommons.holograms;

import de.exceptionflug.mccommons.holograms.line.HologramLine;
import de.exceptionflug.mccommons.holograms.line.ItemHologramLine;
import de.exceptionflug.mccommons.holograms.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Hologram {

	void update();

	void respawn();

	void despawn();

	void despawnFor(final Player player);

	void spawn();

	void spawnFor(final Player player);

	TextHologramLine appendLine(final String text);

	ItemHologramLine appendLine(final ItemStack itemStack);

	void deleteLine(final int index);

	void deleteLine(final HologramLine line);

	TextHologramLine insertLine(final int index, final String text);

	ItemHologramLine insertLine(final int index, final ItemStack stack);

	void teleport(final Location location);

	void setVgap(final double vgap);

	double getHeight();

	double getVgap();

	int getID();

	boolean isDespawned();

	List<HologramLine> getLines();

	Location getLocation();

}
