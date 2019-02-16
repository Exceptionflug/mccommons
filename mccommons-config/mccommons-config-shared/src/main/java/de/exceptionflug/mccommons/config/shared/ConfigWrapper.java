package de.exceptionflug.mccommons.config.shared;

import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface ConfigWrapper {

    <T> T getHandle();

    void set(final String path, final Object obj);
    <T> T getOrSetDefault(final String path, final T def);
    Set<String> getKeys(final String path);
    boolean isSet(final String path);

    default ConfigItemStack getItemStack(final String path, final Map<String, String> replacements) {
        return getItemStack(path, Providers.get(LocaleProvider.class).getFallbackLocale(), replacements);
    }
    default ConfigItemStack getItemStack(final String path, final String... replacements) {
        return getItemStack(path, FormatUtils.createReplacementMap(replacements));
    }
    default ConfigItemStack getItemStack(final String path, final Supplier<String[]> replacer) {
        return getItemStack(path, replacer.get());
    }
    default ConfigItemStack getItemStack(final String path, final Locale locale, final String... replacements) {
        return getItemStack(path, locale, FormatUtils.createReplacementMap(replacements));
    }
    default ConfigItemStack getItemStack(final String path, final Locale locale, final Supplier<String[]> replacer) {
        return getItemStack(path, locale, replacer.get());
    }
    ConfigItemStack getItemStack(final String path, final Locale locale, final Map<String, String> replacements);

    default String getLocalizedString(final Locale locale, final String pathPrefix, final String pathSuffix, final String defaultMessage) {
        if(isSet(pathPrefix+"."+locale.getLanguage()+pathSuffix)) {
            return getOrSetDefault(pathPrefix+"."+locale.getLanguage()+pathSuffix, defaultMessage);
        } else if(!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
            return getLocalizedString(Providers.get(LocaleProvider.class).getFallbackLocale(), pathPrefix, pathSuffix, defaultMessage);
        } else {
            return getOrSetDefault(pathPrefix+"."+locale.getLanguage()+pathSuffix, defaultMessage);
        }
    }

    default List<String> getLocalizedStringList(final Locale locale, final String pathPrefix, final String pathSuffix, final List<String> defaultMessage) {
        if(isSet(pathPrefix+"."+locale.getLanguage()+pathSuffix)) {
            return getOrSetDefault(pathPrefix+"."+locale.getLanguage()+pathSuffix, defaultMessage);
        } else if(!locale.getLanguage().equalsIgnoreCase(Providers.get(LocaleProvider.class).getFallbackLocale().getLanguage())) {
            return getLocalizedStringList(Providers.get(LocaleProvider.class).getFallbackLocale(), pathPrefix, pathSuffix, defaultMessage);
        } else {
            return getOrSetDefault(pathPrefix+"."+locale.getLanguage()+pathSuffix, defaultMessage);
        }
    }

    void save();
    void reload();

}
