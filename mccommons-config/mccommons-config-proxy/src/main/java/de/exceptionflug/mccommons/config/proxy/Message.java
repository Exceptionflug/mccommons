package de.exceptionflug.mccommons.config.proxy;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.config.shared.MessageMode;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.protocolize.world.Sound;
import de.exceptionflug.protocolize.world.SoundCategory;
import de.exceptionflug.protocolize.world.WorldModule;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.*;

public final class Message {

    public static void send(final ProxiedPlayer proxiedPlayer, final ConfigWrapper config, final String messageKey, final String defaultMessage, final String... replacements) {
        send(proxiedPlayer, config, true, messageKey, defaultMessage, replacements);
    }

    public static void send(final ProxiedPlayer proxiedPlayer, final ConfigWrapper config, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
        List<MessageMode> messageModes = Collections.singletonList(MessageMode.DEFAULT);
        if(config.isSet(messageKey+".modes")) {
            messageModes = config.getOrSetDefault(messageKey+".modes", messageModes);
            for(final MessageMode messageMode : messageModes) {
                if(messageMode == MessageMode.DEFAULT) {
                    getMessage1(config, Providers.get(LocaleProvider.class).provide(proxiedPlayer.getUniqueId()), prefix, messageKey, defaultMessage, replacements).forEach(proxiedPlayer::sendMessage);
                } else if(messageMode == MessageMode.RAW) {
                    final BaseComponent[] textComponent = ComponentSerializer.parse(getMessage2(config, Providers.get(LocaleProvider.class).provide(proxiedPlayer.getUniqueId()), prefix, messageKey, defaultMessage, replacements));
                    proxiedPlayer.sendMessage(textComponent);
                } else if(messageMode == MessageMode.TITLE) {
                    final Title title = ProxyServer.getInstance().createTitle();
                    title.fadeIn(config.getOrSetDefault(messageKey+".title.fadeIn", 20));
                    title.fadeOut(config.getOrSetDefault(messageKey+".title.fadeOut", 20));
                    title.stay(config.getOrSetDefault(messageKey+".title.stay", 60));
                    title.title(TextComponent.fromLegacyText(FormatUtils.format(config.getOrSetDefault(messageKey+".title.text", messageKey+".title.text"), replacements)));
                    title.subTitle(TextComponent.fromLegacyText(FormatUtils.format(config.getOrSetDefault(messageKey+".title.subTitle", messageKey+".title.subTitle"), replacements)));
                } else if(messageMode == MessageMode.SOUND) {
                    for(final String key : config.getKeys(messageKey+".sounds")) {
                        final Sound sound = Sound.valueOf(config.getOrSetDefault(messageKey+"."+key+".sound", "ENTITY_EXPERIENCE_ORB_PICKUP"));
                        final SoundCategory category = SoundCategory.getCategory(config.getOrSetDefault(messageKey+"."+key+".category", "master"));
                        final float volume = config.getOrSetDefault(messageKey+"."+key+".volume", 1F);
                        final float pitch = config.getOrSetDefault(messageKey+"."+key+".pitch", 1F);
                        WorldModule.playSound(proxiedPlayer, sound, category, volume, pitch);
                    }
                }
            }
        } else {
            // LEGACY FORMAT
            proxiedPlayer.sendMessage(getMessage(config, Providers.get(LocaleProvider.class).provide(proxiedPlayer.getUniqueId()), prefix, messageKey, defaultMessage, replacements));
        }
    }

    public static void broadcast(final List<ProxiedPlayer> proxiedPlayers, final ConfigWrapper config, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
        for(final ProxiedPlayer proxiedPlayer : proxiedPlayers) {
            send(proxiedPlayer, config, prefix, messageKey, defaultMessage, replacements);
        }
    }

    public static void broadcast(final List<ProxiedPlayer> proxiedPlayers, final ConfigWrapper config, final String messageKey, final String defaultMessage, final String... replacements) {
        for(final ProxiedPlayer proxiedPlayer : proxiedPlayers) {
            send(proxiedPlayer, config, messageKey, defaultMessage, replacements);
        }
    }

    public static void sendTitle(final ProxiedPlayer proxiedPlayer, final ConfigWrapper config, final String messageKey, final String title, final String... replacements) {
        final Locale locale = Providers.get(LocaleProvider.class).provide(proxiedPlayer.getUniqueId());
        String t = config.getOrSetDefault(messageKey+"."+locale.getLanguage()+".title", title);
        t = FormatUtils.format(t, replacements);
        String s = config.getOrSetDefault(messageKey+"."+locale.getLanguage()+".subtitle", title);
        s = FormatUtils.format(s, replacements);
        final int stay = config.getOrSetDefault(messageKey+".stay", 60);
        final int fadeIn = config.getOrSetDefault(messageKey+".fadeIn", 20);
        final int fadeOut = config.getOrSetDefault(messageKey+".fadeOut", 20);
        final Title tit = ProxyServer.getInstance().createTitle();
        tit.fadeIn(fadeIn).fadeOut(fadeOut).stay(stay).subTitle(TextComponent.fromLegacyText(s)).title(TextComponent.fromLegacyText(t));
        tit.send(proxiedPlayer);
    }

    public static String getPrefix(final ConfigWrapper config, final Locale locale) {
        return config.getOrSetDefault("Messages.prefix."+locale.getLanguage(), "[Prefix] ");
    }

    public static String getMessage(final ConfigWrapper config, final Locale locale, final String messageKey, final String defaultMessage, final String... replacements) {
        return getMessage(config, locale, true, messageKey, defaultMessage, replacements);
    }

    public static String getMessage(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
        if(config.isSet(messageKey+"."+locale.getLanguage())) {
            return getString0(config, locale, prefix, messageKey, defaultMessage, replacements);
        } else if(!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
            return getMessage(config, Providers.get(LocaleProvider.class).getFallbackLocale(), messageKey, defaultMessage, replacements);
        } else {
            return getString0(config, locale, prefix, messageKey, defaultMessage, replacements);
        }
    }

    private static String getString0(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String[] replacements) {
        String message = config.getOrSetDefault(messageKey+"."+locale.getLanguage(), defaultMessage);
        message = FormatUtils.format(message, replacements);
        if(prefix)
            message = getPrefix(config, locale)+message;
        return message;
    }

    private static List<String> getMessage1(final ConfigWrapper config, final Locale locale, final String messageKey, final String defaultMessage, final String... replacements) {
        return getMessage1(config, locale, true, messageKey, defaultMessage, replacements);
    }

    private static List<String> getMessage1(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
        if(config.isSet(messageKey+".texts."+locale.getLanguage())) {
            return getString1(config, locale, prefix, messageKey, defaultMessage, replacements);
        } else if(!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
            return getMessage1(config, Providers.get(LocaleProvider.class).getFallbackLocale(), messageKey, defaultMessage, replacements);
        } else {
            return getString1(config, locale, prefix, messageKey, defaultMessage, replacements);
        }
    }

    private static List<String> getString1(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String[] replacements) {
        List<String> messages = config.getOrSetDefault(messageKey+".texts."+locale.getLanguage(), Collections.singletonList(defaultMessage));
        messages = FormatUtils.format(messages, replacements);
        if(prefix) {
            messages = FormatUtils.format(messages, "%prefix%", getPrefix(config, locale));
        }
        return messages;
    }

    private static String getMessage2(final ConfigWrapper config, final Locale locale, final String messageKey, final String defaultMessage, final String... replacements) {
        return getMessage2(config, locale, true, messageKey, defaultMessage, replacements);
    }

    private static String getMessage2(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
        if(config.isSet(messageKey+".raw."+locale.getLanguage())) {
            return getString2(config, locale, prefix, messageKey, defaultMessage, replacements);
        } else if(!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
            return getMessage2(config, Providers.get(LocaleProvider.class).getFallbackLocale(), messageKey, defaultMessage, replacements);
        } else {
            return getString2(config, locale, prefix, messageKey, defaultMessage, replacements);
        }
    }

    private static String getString2(final ConfigWrapper config, final Locale locale, final boolean prefix, final String messageKey, final String defaultMessage, final String[] replacements) {
        String message = config.getOrSetDefault(messageKey+".raw."+locale.getLanguage(), defaultMessage);
        message = FormatUtils.format(message, replacements);
        if(prefix)
            message = getPrefix(config, locale)+message;
        return message;
    }

}
