package de.exceptionflug.mccommons.config.shared;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigFactory {

    private final static Map<Class<? extends ConfigWrapper>, Function<File, ConfigWrapper>> SUPPLIER_MAP = new HashMap<>();
    private final static List<ConfigWrapper> LOADED_CONFIGS = new ArrayList<>();

    public static <T extends ConfigWrapper> T create(final String pluginName, final Class c, final Class<T> configType) {
        return create(new File("plugins/"+pluginName, c.getSimpleName()+".yml"), configType);
    }

    public static <T extends ConfigWrapper> T create(final File file, final Class<T> configType) {
        final Function<File, ConfigWrapper> supplier = getSupplier(configType);
        if(supplier == null)
            throw new NullPointerException("Supplier not found.");
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (final IOException e) {
            }
        }
        T out = (T) supplier.apply(file);
        LOADED_CONFIGS.add(out);
        return out;
    }

    public static void register(final Class<? extends ConfigWrapper> type, final Function<File, ConfigWrapper> wrapperSupplier) {
        SUPPLIER_MAP.put(type, wrapperSupplier);
    }

    public static Function<File, ConfigWrapper> getSupplier(final Class<? extends ConfigWrapper> type) {
        return SUPPLIER_MAP.get(type);
    }

    public static void reloadAll() {
        for(final ConfigWrapper wrapper : LOADED_CONFIGS) {
            wrapper.reload();
        }
    }

}
