package de.exceptionflug.mccommons.holograms.localized;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.holograms.Hologram;
import de.exceptionflug.mccommons.holograms.HologramImpl;
import de.exceptionflug.mccommons.holograms.Holograms;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class LocalizedHologramBuilder {

	private final JavaPlugin mccommons;
	private final ConfigWrapper config;
	private List<LineData> lines = new ArrayList<>();
	private Location location;

	LocalizedHologramBuilder(final JavaPlugin mccommons, final ConfigWrapper config) {
		this.mccommons = mccommons;
		this.config = config;
	}

	public LocalizedHologramBuilder appendLine(final ItemStack itemStack) {
		lines.add(new LineData(itemStack));
		return this;
	}

	public LocalizedHologramBuilder appendLine(final String msgKey, final String defaultMessage) {
		lines.add(new LineData(msgKey, defaultMessage, (p) -> new String[0]));
		return this;
	}

	public LocalizedHologramBuilder appendLine(final String msgKey, final String defaultMessage, final String... nonDynamicReplacements) {
		lines.add(new LineData(msgKey, defaultMessage, (p) -> nonDynamicReplacements));
		return this;
	}

	public LocalizedHologramBuilder appendLine(final String msgKey, final String defaultMessage, final PlayerboundReplacementSupplier replacementSupplier) {
		lines.add(new LineData(msgKey, defaultMessage, replacementSupplier));
		return this;
	}

	public LocalizedHologramBuilder setLines(final List<LineData> lines) {
		this.lines = lines;
		return this;
	}

	public LocalizedHologramBuilder setLocation(final Location location) {
		this.location = location;
		return this;
	}

	public LocalizedHologram build() {
		Preconditions.checkNotNull(location, "The location cannot be null!");
		final Hologram out = Holograms.createHologram(location);
		final List<Integer> ids = new ArrayList<>();
		for (final LineData line : lines) {
			if (line.getContent() instanceof ItemStack) {
				ids.add(out.appendLine((ItemStack) line.getContent()).getEntityID());
			} else if (line.getContent() instanceof String) {
				ids.add(out.appendLine("{!}" + line.getContent()).getEntityID());
			}
		}
		return new LocalizedHologramImpl((HologramImpl) out, new PacketListener(mccommons, config, lines, ids));
	}

}
