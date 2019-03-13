package de.exceptionflug.mccommons.config.proxy;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Locale;

public final class Message {

    public static void send(final ProxiedPlayer proxiedPlayer, final ConfigWrapper config, final String messageKey, final String defaultMessage, final String... replacements) {
        send(proxiedPlayer, config, true, messageKey, defaultMessage, replacements);
    }

    public static void send(final ProxiedPlayer proxiedPlayer, final ConfigWrapper config, final boolean prefix, final String messageKey, final String defaultMessage, final String... replacements) {
        proxiedPlayer.sendMessage(getMessage(config, Providers.get(LocaleProvider.class).provide(proxiedPlayer.getUniqueId()), prefix, messageKey, defaultMessage, replacements));
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

}
