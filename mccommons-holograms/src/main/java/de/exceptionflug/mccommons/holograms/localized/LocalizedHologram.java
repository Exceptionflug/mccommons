package de.exceptionflug.mccommons.holograms.localized;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.holograms.Hologram;

public interface LocalizedHologram extends Hologram {

	ConfigWrapper getMessageConfig();

	void appendLine(final String msgKey, final String defaultMessage, final PlayerboundReplacementSupplier replacementSupplier);

	void appendLine(final String msgKey, final String defaultMessage, final String... nonDynamicReplacements);

	void appendLine(final String msgKey, final String defaultMessage);
}
