package de.exceptionflug.mccommons.config.remote.server.controllers.impl;

import de.exceptionflug.mccommons.config.proxy.ProxyConfigProxyYamlConfigWrapper;
import de.exceptionflug.mccommons.config.remote.model.ConfigData;
import de.exceptionflug.mccommons.config.remote.server.controllers.ConfigRestController;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class ConfigRestControllerImpl implements ConfigRestController {

	private static final Logger LOGGER = LogManager.getLogger(ConfigRestControllerImpl.class);

	@Override
	public ConfigData getConfig(final String path) throws IOException {
		final ConfigData configData = new ConfigData();
		final File file = new File(new File("configs"), path);
		configData.setRemotePath(path);
		if (file.exists()) {
			configData.setConfigData(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
			configData.setLastUpdate(Files.getLastModifiedTime(file.toPath()).toMillis());
		}
		return configData;
	}

	@Override
	public void update(final ConfigData configData) throws IOException {
		final File file = new File(new File("configs"), configData.getRemotePath());
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		if (file.lastModified() > configData.getLastUpdate())
			throw new IllegalStateException("Unable to save outdated config state.");
		final ConfigWrapper configWrapper = new ProxyConfigProxyYamlConfigWrapper(configData, s -> {
			try (final FileWriter fileWriter = new FileWriter(file)) {
				fileWriter.write(s.getConfigData());
				fileWriter.flush();
			} catch (final IOException e) {
				LOGGER.error("Exception occurred while saving config to " + configData.getRemotePath(), e);
			}
		}, () -> null);
		configWrapper.save();
	}

}
