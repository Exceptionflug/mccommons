package de.exceptionflug.mccommons.inventories.proxy.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.protocolize.inventory.InventoryType;

public class ProtocolizeInventoryTypeConverter implements Converter<de.exceptionflug.mccommons.inventories.api.InventoryType, InventoryType> {

	@Override
	public InventoryType convert(final de.exceptionflug.mccommons.inventories.api.InventoryType src) {
		return InventoryType.valueOf(src.name());
	}

}
