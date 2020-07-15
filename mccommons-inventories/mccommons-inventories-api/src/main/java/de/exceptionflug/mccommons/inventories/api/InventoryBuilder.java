package de.exceptionflug.mccommons.inventories.api;

import java.util.Map;
import java.util.UUID;

/**
 * The {@link InventoryBuilder} is responsible to build a platform dependent inventory using a given {@link InventoryWrapper}. This class also manages the registry of current {@link InventoryWrapper}s.
 */
public interface InventoryBuilder {

	<T> T build(final T prebuild, final InventoryWrapper wrapper);

	InventoryWrapper getWrapperByHandle(final Object handle);

	void destroyWrappersBy(final UUID uniqueId);

	Map.Entry<InventoryWrapper, Long> getLastBuild(final UUID uniqueId);

	void uncache(final InventoryWrapper wrapper);

	void open(final InventoryWrapper wrapper);

	void addWrapper(final InventoryWrapper wrapper);

}
