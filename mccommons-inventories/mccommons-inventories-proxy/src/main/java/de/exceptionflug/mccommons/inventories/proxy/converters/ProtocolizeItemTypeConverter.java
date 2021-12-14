package de.exceptionflug.mccommons.inventories.proxy.converters;

import de.exceptionflug.mccommons.core.Converter;
import dev.simplix.protocolize.data.ItemType;

public class ProtocolizeItemTypeConverter implements Converter<ItemType, de.exceptionflug.mccommons.inventories.api.item.ItemType> {

	@Override
	public de.exceptionflug.mccommons.inventories.api.item.ItemType convert(final ItemType src) {
		return de.exceptionflug.mccommons.inventories.api.item.ItemType.valueOf(src.name());
	}

}
