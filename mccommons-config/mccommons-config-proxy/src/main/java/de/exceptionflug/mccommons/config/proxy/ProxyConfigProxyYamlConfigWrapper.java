package de.exceptionflug.mccommons.config.proxy;

import de.exceptionflug.mccommons.config.remote.model.ConfigData;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ProxyConfigProxyYamlConfigWrapper implements ProxyConfig {

	private Configuration configuration;
	private final File file;
	private ConfigData configData;
	private final Consumer<ConfigData> updateConsumer;
	private final Supplier<ConfigData> reloadSupplier;

	public ProxyConfigProxyYamlConfigWrapper(final File file) {
		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.file = file;
		this.updateConsumer = null;
		this.reloadSupplier = null;
	}

	public ProxyConfigProxyYamlConfigWrapper(final ConfigData configData, final Consumer<ConfigData> updateConsumer, final Supplier<ConfigData> reloadSupplier) {
		configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configData.getConfigData());
		this.file = null;
		this.updateConsumer = updateConsumer;
		this.configData = configData;
		this.reloadSupplier = reloadSupplier;
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
			if (configData != null) {
				try (final StringWriter stringWriter = new StringWriter()) {
					ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, stringWriter);
					configData.setConfigData(stringWriter.toString());
					updateConsumer.accept(configData);
					return;
				}
			}
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reload() {
		try {
			if (configData != null) {
				setConfigData(reloadSupplier.get());
				return;
			}
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ConfigData getConfigData() {
		return configData;
	}

	@Override
	public void setConfigData(final ConfigData configData) {
		this.configData = configData;
		configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configData.getConfigData());
	}

}
