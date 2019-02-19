package de.exceptionflug.mccommons.config.proxy;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProxyConfigProxyYamlConfigWrapper implements ProxyConfig {

    private Configuration configuration;
    private final File file;

    public ProxyConfigProxyYamlConfigWrapper(final File file) {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.file = file;
    }

    @Override
    public <T> T getHandle() {
        return (T) configuration;
    }

    @Override
    public void set(final String path, final Object obj) {
        configuration.set(path, obj);
        save();
    }

    @Override
    public <T> T getOrSetDefault(String path, T def) {
        final Object o = configuration.get(path);
        if (o != null)
            return (T) o;
        if (def != null) {
            configuration.set(path, def);
            save();
        }
        return def;
    }

    @Override
    public Set<String> getKeys(String path) {
        return new HashSet<>(configuration.getSection(path).getKeys());
    }

    @Override
    public boolean isSet(String path) {
        return configuration.contains(path);
    }

    @Override
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
