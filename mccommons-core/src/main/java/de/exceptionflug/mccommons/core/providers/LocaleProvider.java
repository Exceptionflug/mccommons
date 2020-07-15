package de.exceptionflug.mccommons.core.providers;

import java.util.Locale;
import java.util.UUID;

public abstract class LocaleProvider {

	public abstract Locale provide(UUID uuid);

	public abstract Locale getFallbackLocale();

}
