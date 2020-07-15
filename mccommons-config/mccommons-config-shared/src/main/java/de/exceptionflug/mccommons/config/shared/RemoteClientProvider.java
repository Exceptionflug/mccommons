package de.exceptionflug.mccommons.config.shared;

import de.exceptionflug.mccommons.config.remote.client.RemoteConfigClient;

public abstract class RemoteClientProvider {

	public abstract RemoteConfigClient get();

}
