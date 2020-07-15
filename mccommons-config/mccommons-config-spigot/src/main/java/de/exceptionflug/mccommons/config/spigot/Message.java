package de.exceptionflug.mccommons.config.spigot;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.config.shared.MessageMode;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.util.*;
import java.util.stream.Collectors;

public final class Message {

	public static void send(final CommandSender sender, final ConfigWrapper config, final String messageKey, final String defaultMessage, final String... replacements) {
		send(sender, config, true, messageKey, defaultMessage, replacements);
	}

	public static void send(final CommandSender sender, final ConfigWrapper config, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
		List<String> messageModes = Collections.singletonList("DEFAULT");
		final UUID uuid;
		final Player player;
		if (sender instanceof Player) {
			uuid = ((Player) sender).getUniqueId();
			player = (Player) sender;
		} else {
			uuid = new UUID(0, 0);
			player = null;
		}
		if (config.isSet(messageKey + ".modes")) {
			messageModes = config.getOrSetDefault(messageKey + ".modes", messageModes);
			for (final String i : messageModes) {
				final MessageMode messageMode = MessageMode.valueOf(i);
				if (messageMode == MessageMode.DEFAULT) {
					getMessage1(config, Providers.get(LocaleProvider.class).provide(uuid), prefix, messageKey, defaultMessage, replacements).forEach(sender::sendMessage);
				} else if (messageMode == MessageMode.RAW) {
					final BaseComponent[] textComponent = ComponentSerializer.parse(getMessage2(config, Providers.get(LocaleProvider.class).provide(uuid), messageKey, defaultMessage, replacements));
					if (prefix) {
						final BaseComponent[] component = TextComponent.fromLegacyText(getPrefix(config, Providers.get(LocaleProvider.class).provide(uuid)));
						final List<BaseComponent> c = new ArrayList<>(Arrays.asList(component));
						c.addAll(Arrays.asList(textComponent));
						if (player != null) {
							player.spigot().sendMessage(c.toArray(new BaseComponent[0]));
						} else {
							sender.sendMessage(c.stream().map(it -> it.toLegacyText()).collect(Collectors.joining()));
						}
						continue;
					}
					if (player != null) {
						player.spigot().sendMessage(textComponent);
					} else {
						sender.sendMessage(Arrays.stream(textComponent).map(it -> it.toLegacyText()).collect(Collectors.joining()));
					}
				} else if (messageMode == MessageMode.TITLE) {
					Title title = new Title(TextComponent.fromLegacyText(FormatUtils.format(config.getOrSetDefault(messageKey + ".title.text", messageKey + ".title.text"), replacements)),
						TextComponent.fromLegacyText(FormatUtils.format(config.getOrSetDefault(messageKey + ".title.subTitle", messageKey + ".title.subTitle"), replacements)),
						config.getOrSetDefault(messageKey + ".title.fadeIn", 20),
						config.getOrSetDefault(messageKey + ".title.stay", 60),
						config.getOrSetDefault(messageKey + ".title.fadeOut", 20));
					player.sendTitle(title);
				} else if (messageMode == MessageMode.SOUND) {
					for (final String key : config.getKeys(messageKey + ".sounds")) {
						final Sound sound = Sound.valueOf(config.getOrSetDefault(messageKey + ".sounds." + key + ".sound", "CLICK"));
						final double volume = config.getOrSetDefault(messageKey + ".sounds." + key + ".volume", 1D);
						final double pitch = config.getOrSetDefault(messageKey + ".sounds." + key + ".pitch", 1D);
						if (player != null) {
							player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
						}
					}
				} else if (messageMode == MessageMode.COMPONENT) {
					final BaseComponent baseComponent = getComponent0(config, Providers.get(LocaleProvider.class).provide(uuid), messageKey, new TextComponent(defaultMessage), replacements);
					if (player != null) {
						player.spigot().sendMessage(baseComponent);
					} else {
						sender.sendMessage(baseComponent.toLegacyText());
					}
				}
			}
		} else {
			// LEGACY FORMAT
			sender.sendMessage(getMessage(config, Providers.get(LocaleProvider.class).provide(uuid), prefix, messageKey, defaultMessage, replacements));
		}
	}

	public static void broadcast(final Collection<? extends Player> players, final ConfigWrapper config, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
		for (final Player player : players) {
			send(player, config, prefix, messageKey, defaultMessage, replacements);
		}
	}

	public static void broadcast(final Collection<? extends Player> players, final ConfigWrapper config, final String messageKey, final String defaultMessage, final String... replacements) {
		for (final Player player : players) {
			send(player, config, messageKey, defaultMessage, replacements);
		}
	}

	public static String getPrefix(final ConfigWrapper config, final Locale locale) {
		return config.getOrSetDefault("Messages.prefix." + locale.getLanguage(), "[Prefix] ");
	}

	public static String getMessage(final ConfigWrapper config, final Locale locale, final String messageKey, final String defaultMessage, final String... replacements) {
		return getMessage(config, locale, true, messageKey, defaultMessage, replacements);
	}

	public static String getMessage(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
		if (config.isSet(messageKey + "." + locale.getLanguage())) {
			return getString0(config, locale, prefix, messageKey, defaultMessage, replacements);
		} else if (!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
			return getMessage(config, Providers.get(LocaleProvider.class).getFallbackLocale(), messageKey, defaultMessage, replacements);
		} else {
			return getString0(config, locale, prefix, messageKey, defaultMessage, replacements);
		}
	}

	private static BaseComponent getComponent0(final ConfigWrapper config, final Locale locale, final String messageKey, final TextComponent defaultMessage, final String[] replacements) {
		return getComponent1(config, messageKey + "." + locale.getLanguage(), defaultMessage, replacements);
	}

	private static BaseComponent getComponent1(final ConfigWrapper config, final String messageKey, final TextComponent defaultMessage, final String[] replacements) {
		final String text = config.getOrSetDefault(messageKey + ".text", defaultMessage.getText());
		final List<BaseComponent> extras = new ArrayList<>();
		for (final String key : config.getKeys(messageKey + ".extras")) {
			extras.add(getComponent1(config, messageKey + ".extras." + key, new TextComponent(), replacements));
		}
		final TextComponent out = new TextComponent(text);
		out.setBold(config.getOrSetDefault(messageKey + ".bold", defaultMessage.isBold()));
		out.setItalic(config.getOrSetDefault(messageKey + ".italic", defaultMessage.isItalic()));
		out.setObfuscated(config.getOrSetDefault(messageKey + ".obfuscated", defaultMessage.isObfuscated()));
		out.setStrikethrough(config.getOrSetDefault(messageKey + ".strikethrough", defaultMessage.isStrikethrough()));
		out.setUnderlined(config.getOrSetDefault(messageKey + ".underlined", defaultMessage.isUnderlined()));
		out.setInsertion(config.getOrSetDefault(messageKey + ".insertion", defaultMessage.getInsertion()));
		out.setColor(ChatColor.valueOf(FormatUtils.format(config.getOrSetDefault(messageKey + ".color", defaultMessage.getColor().name()), replacements)));
		if (config.isSet(messageKey + ".clickEvent")) {
			final ClickEvent.Action action = ClickEvent.Action.valueOf(config.getOrSetDefault(messageKey + ".clickEvent.action", ClickEvent.Action.RUN_COMMAND.name()));
			final String value = config.getOrSetDefault(messageKey + ".clickEvent.value", "value goes here");
			out.setClickEvent(new ClickEvent(action, FormatUtils.format(value, replacements)));
		} else if (defaultMessage.getClickEvent() != null) {
			config.set(messageKey + ".clickEvent.action", defaultMessage.getClickEvent().getAction().name());
			config.set(messageKey + ".clickEvent.value", defaultMessage.getClickEvent().getValue());
		}
		if (config.isSet(messageKey + ".hoverEvent")) {
			final HoverEvent.Action action = HoverEvent.Action.valueOf(config.getOrSetDefault(messageKey + ".hoverEvent.action", HoverEvent.Action.SHOW_TEXT.name()));
			final BaseComponent value = getComponent1(config, messageKey + ".hoverEvent.value", new TextComponent(), replacements);
			out.setHoverEvent(new HoverEvent(action, new BaseComponent[]{value}));
		} else if (defaultMessage.getHoverEvent() != null) {
			config.set(messageKey + ".hoverEvent.action", out.getHoverEvent().getAction().name());
			getComponent1(config, messageKey + ".hoverEvent.value", (TextComponent) out.getHoverEvent().getValue()[0], replacements);
		}
		extras.forEach(out::addExtra);
		out.setText(FormatUtils.formatAmpersandColorCodes(FormatUtils.format(out.getText(), replacements)));
		return out;
	}

	private static String getString0(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String[] replacements) {
		String message = config.getOrSetDefault(messageKey + "." + locale.getLanguage(), defaultMessage);
		message = FormatUtils.format(message, replacements);
		if (prefix) {
			message = getPrefix(config, locale) + message;
		}
		return message;
	}

	private static List<String> getMessage1(final ConfigWrapper config, final Locale locale, final String messageKey, final String defaultMessage, final String... replacements) {
		return getMessage1(config, locale, true, messageKey, defaultMessage, replacements);
	}

	private static List<String> getMessage1(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
		if (config.isSet(messageKey + ".texts." + locale.getLanguage())) {
			return getString1(config, locale, prefix, messageKey, defaultMessage, replacements);
		} else if (!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
			return getMessage1(config, Providers.get(LocaleProvider.class).getFallbackLocale(), messageKey, defaultMessage, replacements);
		} else {
			return getString1(config, locale, prefix, messageKey, defaultMessage, replacements);
		}
	}

	private static List<String> getString1(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String[] replacements) {
		List<String> messages = config.getOrSetDefault(messageKey + ".texts." + locale.getLanguage(), Collections.singletonList(defaultMessage));
		messages = FormatUtils.format(messages, replacements);
		if (prefix) {
			messages = FormatUtils.format(messages, "%prefix%", getPrefix(config, locale));
		}
		return messages;
	}


	private static String getMessage2(final ConfigWrapper config, final Locale locale, final String messageKey, final String defaultMessage, final String... replacements) {
		if (config.isSet(messageKey + ".raw." + locale.getLanguage())) {
			return getString2(config, locale, messageKey, defaultMessage, replacements);
		} else if (!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
			return getMessage2(config, Providers.get(LocaleProvider.class).getFallbackLocale(), messageKey, defaultMessage, replacements);
		} else {
			return getString2(config, locale, messageKey, defaultMessage, replacements);
		}
	}

	private static String getString2(final ConfigWrapper config, final Locale locale, final String messageKey, final String defaultMessage, final String[] replacements) {
		String message = config.getOrSetDefault(messageKey + ".raw." + locale.getLanguage(), defaultMessage);
		message = FormatUtils.format(message, replacements);
		return message;
	}

}
