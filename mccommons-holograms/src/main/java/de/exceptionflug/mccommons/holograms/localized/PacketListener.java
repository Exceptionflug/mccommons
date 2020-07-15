package de.exceptionflug.mccommons.holograms.localized;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;

final class PacketListener extends PacketAdapter {

	private final ConfigWrapper config;
	private final List<LineData> handleLines;
	private final List<Integer> lineIds;

	public PacketListener(final JavaPlugin plugin, final ConfigWrapper config, final List<LineData> handleLines, final List<Integer> lineIds) {
		super(plugin, Server.ENTITY_METADATA);
		this.config = config;
		this.handleLines = handleLines;
		this.lineIds = lineIds;
	}

	@Override
	public void onPacketSending(final PacketEvent event) {
		final WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata(event.getPacket().deepClone());
		if (!lineIds.contains(meta.getEntityID()))
			return;
		final List<WrappedWatchableObject> metadata = meta.getMetadata();
		editMetadata(metadata, event.getPlayer());
		meta.setMetadata(metadata);
		event.setPacket(meta.getHandle());
	}

	private void editMetadata(final List<WrappedWatchableObject> metadata, final Player player) {
		for (final WrappedWatchableObject object : metadata) {
			if (object.getValue() instanceof String) {
				final String value = (String) object.getValue();
				if (value.startsWith("{!}")) { // Ensure mccommons placeholder
					final String key = value.substring(3);
					final LineData line = getLine(key);
					if (line != null) {
						final String message = getLocalizedMessage(Providers.get(LocaleProvider.class).provide(player.getUniqueId()), key, line.getDefaultMessage(), line.getReplacementSupplier().get(player));
						object.setValue(message);
					}
				}
			}
		}
	}

	public LineData getLine(final String key) {
		for (final LineData line : handleLines) {
			if (line.getContent() instanceof String) {
				if (key.equals(line.getContent()))
					return line;
			}
		}
		return null;
	}

	private String getLocalizedMessage(final Locale locale, final String messageKey, final String defaultMessage, final String... replacements) {
		if (config.isSet(messageKey + "." + locale.getLanguage())) {
			return getString0(locale, messageKey, defaultMessage, replacements);
		} else if (!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
			return getLocalizedMessage(Providers.get(LocaleProvider.class).getFallbackLocale(), messageKey, defaultMessage, replacements);
		} else {
			return getString0(locale, messageKey, defaultMessage, replacements);
		}
	}

	private String getString0(final Locale locale, final String messageKey, final String defaultMessage, final String[] replacements) {
		String message = config.getOrSetDefault(messageKey + "." + locale.getLanguage(), defaultMessage);
		message = FormatUtils.format(message, replacements);
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public ConfigWrapper getConfig() {
		return config;
	}

	public List<LineData> getHandleLines() {
		return handleLines;
	}

}
