package de.exceptionflug.mccommons.core;

import java.util.*;

public class Converters {

    private static final Map<Map.Entry<Class, Class>, Converter> CONVERTER_MAP = new HashMap<>();

    public static <T> T convert(final Object source, final Class<T> to) {
        Converter converter = getConverter(source.getClass(), to);
        if(converter == null)
            converter = getMultiConverter(source.getClass(), to);
        if(converter == null)
            throw new NullPointerException("No converter available to convert "+source.getClass().getName()+" to "+to.getName());
        return (T) converter.convert(source);
    }

    private static <S, T> Converter<S, T> getMultiConverter(final Class<S> source, final Class<T> to) {
        final List<Converter> converters = new ArrayList<>();
        findConversionRoute(converters, source, to);
        if(converters.size() <= 1)
            return null;
        return src -> {
            Object out = src;
            for(final Converter converter : converters) {
                out = converter.convert(out);
            }
            return (T) out;
        };
    }

    private static boolean findConversionRoute(final List<Converter> converters, final Class<?> source, final Class<?> to) {
        for(final Map.Entry<Class, Class> entry : CONVERTER_MAP.keySet()) {
            if(entry.getKey().equals(source)) {
                final List<Converter> list = new ArrayList<>();
                if(buildRoute(list, entry, to)) {
                    list.add(CONVERTER_MAP.get(entry));
                    Collections.reverse(list);
                    converters.addAll(list);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean buildRoute(final List<Converter> converters, final Map.Entry<Class, Class> entry, final Class<?> to) {
        if(entry.getValue().equals(to))
            return true;
        for(final Map.Entry<Class, Class> entry1 : CONVERTER_MAP.keySet()) {
            if(entry1.getKey().equals(entry.getValue())) {
                if(buildRoute(converters, entry1, to)) {
                    converters.add(CONVERTER_MAP.get(entry1));
                    return true;
                }
            }
        }
        return false;
    }

    public static void register(final Class<?> sourceType, final Class targetType, final Converter converter) {
        CONVERTER_MAP.put(new AbstractMap.SimpleEntry<>(sourceType, targetType), converter);
    }

    public static <S, T> Converter<S, T> getConverter(final Class<S> sourceType, final Class<T> targetType) {
        return CONVERTER_MAP.get(new AbstractMap.SimpleEntry<>(sourceType, targetType));
    }

}
