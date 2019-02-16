package de.exceptionflug.mccommons.inventories.api;

import java.util.Map;
import java.util.UUID;

public interface InventoryBuilder {

    <T> T build(final T prebuild, final InventoryWrapper wrapper);
    InventoryWrapper getWrapperByHandle(final Object handle);
    void destroyWrappersBy(final UUID uniqueId);
    Map.Entry<InventoryWrapper, Long> getLastBuild(final UUID uniqueId);
    void uncache(final InventoryWrapper wrapper);
    void open(final InventoryWrapper wrapper);

}
