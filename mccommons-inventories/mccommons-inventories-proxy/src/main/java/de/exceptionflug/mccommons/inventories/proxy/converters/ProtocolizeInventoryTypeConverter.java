package de.exceptionflug.mccommons.inventories.proxy.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.protocolize.inventory.InventoryType;

public class ProtocolizeInventoryTypeConverter implements Converter<InventoryType, de.exceptionflug.mccommons.inventories.api.InventoryType> {

    @Override
    public de.exceptionflug.mccommons.inventories.api.InventoryType convert(final InventoryType src) {
        return de.exceptionflug.mccommons.inventories.api.InventoryType.valueOf(src.name());
    }

}
