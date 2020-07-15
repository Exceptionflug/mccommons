package de.exceptionflug.mccommons.config.shared;

import de.exceptionflug.mccommons.config.remote.model.ConfigData;

public interface RemoteConfigWrapper extends ConfigWrapper {

	ConfigData getConfigData();

	void setConfigData(final ConfigData configData);

}
