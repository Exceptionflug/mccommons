package de.exceptionflug.mccommons.config.shared;

import de.exceptionflug.mccommons.config.remote.client.RemoteConfigClient;
import de.exceptionflug.mccommons.config.remote.model.ConfigData;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.WorkingDirectoryProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class ConfigFactory {

    private final static Map<Class<? extends ConfigWrapper>, Function<File, ConfigWrapper>> SUPPLIER_MAP = new HashMap<>();
    private final static Map<Class<? extends ConfigWrapper>, Function<ConfigData, ConfigWrapper>> REMOTE_SUPPLIER_MAP = new HashMap<>();
    private final static Map<String, ConfigWrapper> LOADED_CONFIGS = new ConcurrentHashMap<>();
    private static RemoteConfigClient remoteConfigClient;

    public static <T extends ConfigWrapper> T createRemote(final String pluginName, final Class c, final Class<T> configType) {
        return createRemote("plugins/"+pluginName+"/"+c.getSimpleName()+".yml", configType);
    }

    public static <T extends ConfigWrapper> T createRemote(final String remotePath, final Class<T> configType) {
        final Function<ConfigData, ConfigWrapper> supplier = getRemoteSupplier(configType);
        if(supplier == null)
            throw new NullPointerException("Remote supplier not found.");
        final ConfigData configData = getRemoteConfigClient().getConfigService().getConfig(remotePath).doOnError(throwable -> LogManager.getLogManager().getLogger("ConfigFactory").log(Level.SEVERE, "[MCCommons] Exception while getting remote config. Returning fallback config...", throwable)).blockingFirst();
        if(configData == null) {
            return create(new File(new File(Providers.get(WorkingDirectoryProvider.class).getWorkingDirectory(), "RemoteFallback/"), remotePath), configType);
        }
        T out = (T) supplier.apply(configData);
        LOADED_CONFIGS.put(remotePath, out);
        return out;
    }

    public static <T extends ConfigWrapper> T create(final String pluginName, final Class c, final Class<T> configType) {
        return create(new File("plugins/"+pluginName, c.getSimpleName()+".yml"), configType);
    }

    public static <T extends ConfigWrapper> T create(final File file, final Class<T> configType) {
        if(LOADED_CONFIGS.containsKey(file.getAbsolutePath()))
            return (T) LOADED_CONFIGS.get(file.getAbsolutePath());
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
        LOADED_CONFIGS.put(file.getAbsolutePath(), out);
        return out;
    }

    public static void register(final Class<? extends ConfigWrapper> type, final Function<File, ConfigWrapper> wrapperSupplier) {
        SUPPLIER_MAP.put(type, wrapperSupplier);
    }

    public static void registerRemote(final Class<? extends ConfigWrapper> type, final Function<ConfigData, ConfigWrapper> wrapperSupplier) {
        REMOTE_SUPPLIER_MAP.put(type, wrapperSupplier);
    }

    public static Function<File, ConfigWrapper> getSupplier(final Class<? extends ConfigWrapper> type) {
        return SUPPLIER_MAP.get(type);
    }

    public static Function<ConfigData, ConfigWrapper> getRemoteSupplier(final Class<? extends ConfigWrapper> type) {
        return REMOTE_SUPPLIER_MAP.get(type);
    }

    public static void unload(final ConfigWrapper wrapper) {
        LOADED_CONFIGS.entrySet().stream().filter(it -> it.getValue().equals(wrapper)).map(Map.Entry::getKey).forEach(LOADED_CONFIGS::remove);
    }

    public static void reloadAll() {
        for(final ConfigWrapper wrapper : LOADED_CONFIGS.values()) {
            wrapper.reload();
        }
    }

    public static RemoteConfigClient getRemoteConfigClient() {
        if(remoteConfigClient == null) {
            remoteConfigClient = Providers.get(RemoteClientProvider.class).get();
            Executors.newSingleThreadExecutor().execute(() -> {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(10000);
                        for(final ConfigWrapper wrapper : LOADED_CONFIGS.values()) {
                            if(wrapper instanceof RemoteConfigWrapper) {
                                final ConfigData current = ((RemoteConfigWrapper) wrapper).getConfigData();
                                if(current != null) {
                                    getRemoteConfigClient().getConfigService().getConfig(current.getRemotePath()).subscribe(configData -> {
                                        if(configData.getLastUpdate() > current.getLastUpdate()) {
                                            ((RemoteConfigWrapper) wrapper).setConfigData(configData);
                                        }
                                    }, throwable -> {
                                        LogManager.getLogManager().getLogger("ConfigFactory").log(Level.SEVERE, "[MCCommons] Exception while refreshing "+current.getRemotePath()+" from remote server", throwable);
                                    });
                                }
                            }
                        }
                    } catch (final InterruptedException e) {
                    } catch (final Exception e) {
                        LogManager.getLogManager().getLogger("ConfigFactory").log(Level.SEVERE, "[MCCommons] Exception while refreshing configs from remote server", e);
                    }
                }
            });
        }
        return remoteConfigClient;
    }
}
