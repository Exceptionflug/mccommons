package de.exceptionflug.mccommons.holograms;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.core.utils.ConcurrentLinkedList;
import de.exceptionflug.mccommons.holograms.line.HologramLine;
import de.exceptionflug.mccommons.holograms.line.ItemHologramLine;
import de.exceptionflug.mccommons.holograms.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HologramImpl implements Hologram {

	private final static AtomicInteger ID_GENERATOR = new AtomicInteger();

	private final List<HologramLine> lines = new ConcurrentLinkedList<>();
	private final int id;

	private Location location;
	private double vgap = 0;
	private boolean despawned;

	public HologramImpl(final Location location) {
		this.location = location;
		id = ID_GENERATOR.getAndIncrement();
	}

	@Override
	public void update() {
		lines.forEach(HologramLine::update);
	}

	@Override
	public void respawn() {
		despawn();
		spawn();
	}

	@Override
	public void despawn() {
		lines.forEach(HologramLine::despawn);
		Holograms.KNOWN_HOLOGRAMS.remove(this);
		despawned = true;
	}

	@Override
	public void despawnFor(final Player player) {
		lines.forEach(it -> it.despawnFor(player));
	}

	@Override
	public void spawn() {
		Location location = this.location.clone().add(0, getHeight(), 0);
		for (final HologramLine line : lines) {
			line.spawn(location);
			location = location.subtract(0, line.getHeight() + vgap, 0);
		}
		if (!Holograms.KNOWN_HOLOGRAMS.contains(this))
			Holograms.KNOWN_HOLOGRAMS.add(this);
		update();
		despawned = false;
	}

	@Override
	public void spawnFor(final Player player) {
		if (!location.getWorld().getUID().equals(player.getWorld().getUID())) {
			return;
		}
		final double distance = location.distance(player.getLocation());
		if (distance > Holograms.hologramTrackingDistance + 5) {
			return;
		}
		if (despawned) {
			return;
		}
		Location location = this.location.clone().add(0, getHeight(), 0);
		for (final HologramLine line : lines) {
			line.spawnFor(location, player);
			location = location.subtract(0, line.getHeight() + vgap, 0);
		}
		if (!Holograms.KNOWN_HOLOGRAMS.contains(this))
			Holograms.KNOWN_HOLOGRAMS.add(this);
		update();
	}

	@Override
	public TextHologramLine appendLine(final String text) {
		Preconditions.checkNotNull(text, "The text cannot be null!");
		final TextHologramLine e = new TextHologramLine(text);
		lines.add(e);
		respawn();
		return e;
	}

	@Override
	public ItemHologramLine appendLine(final ItemStack itemStack) {
		Preconditions.checkNotNull(itemStack, "The itemStack cannot be null!");
		final ItemHologramLine e = new ItemHologramLine(itemStack);
		lines.add(e);
		respawn();
		return e;
	}

	@Override
	public void deleteLine(final int index) {
		lines.remove(index);
		respawn();
	}

	@Override
	public void deleteLine(final HologramLine line) {
		lines.remove(line);
		respawn();
	}

	@Override
	public TextHologramLine insertLine(final int index, final String text) {
		Preconditions.checkNotNull(text, "The text cannot be null!");
		final TextHologramLine element = new TextHologramLine(text);
		lines.set(index, element);
		respawn();
		return element;
	}

	@Override
	public ItemHologramLine insertLine(final int index, final ItemStack stack) {
		Preconditions.checkNotNull(stack, "The stack cannot be null!");
		final ItemHologramLine element = new ItemHologramLine(stack);
		lines.add(index, element);
		respawn();
		return element;
	}

	@Override
	public void teleport(final Location location) {
		Preconditions.checkNotNull(location, "The location cannot be null!");
		this.location = location;
		Location loc = this.location.clone().add(0, getHeight(), 0);
		for (final HologramLine line : lines) {
			line.teleport(loc);
			loc = loc.subtract(0, line.getHeight() + vgap, 0);
		}
	}

	@Override
	public void setVgap(final double vgap) {
		this.vgap = vgap;
	}

	@Override
	public double getHeight() {
		double out = 0;
		for (int i = 0; i < lines.size(); i++) {
			final HologramLine line = lines.get(i);
			out += line.getHeight();
			if (i != lines.size() - 1)
				out += vgap;
		}
		return out;
	}

	@Override
	public double getVgap() {
		return vgap;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public boolean isDespawned() {
		return despawned;
	}

	@Override
	public List<HologramLine> getLines() {
		return lines;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HologramImpl hologram = (HologramImpl) o;
		return id == hologram.id &&
			Double.compare(hologram.vgap, vgap) == 0 &&
			despawned == hologram.despawned &&
			location.equals(hologram.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, location, vgap, despawned);
	}

	@Override
	public String toString() {
		return "HologramImpl{" +
			"lines=" + lines +
			", id=" + id +
			", location=" + location +
			", vgap=" + vgap +
			", despawned=" + despawned +
			'}';
	}
}
