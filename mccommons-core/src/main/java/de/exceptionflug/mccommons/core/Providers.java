package de.exceptionflug.mccommons.core;

import de.exceptionflug.mccommons.core.providers.LocaleProvider;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Providers {

    private static final Map<Class, Object> CLASS_OBJECT_MAP = new ConcurrentHashMap<>();

    static {
        register(LocaleProvider.class, new LocaleProvider() {
            @Override
            public Locale provide(UUID uuid) {
                return Locale.GERMANY;
            }

            @Override
            public Locale getFallbackLocale() {
                return Locale.GERMANY;
            }
        });
    }

    public static void register(final Class type, final Object obj) {
        CLASS_OBJECT_MAP.put(type, obj);
    }

    public static  <T> T get(final Class<T> type) {
        return (T) CLASS_OBJECT_MAP.get(type);
    }

    public static boolean has(final Class type) {
        return CLASS_OBJECT_MAP.containsKey(type);
    }

}
