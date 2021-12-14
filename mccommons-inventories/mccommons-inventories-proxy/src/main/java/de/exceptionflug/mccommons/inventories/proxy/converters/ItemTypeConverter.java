package de.exceptionflug.mccommons.inventories.proxy.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;

public class ItemTypeConverter implements Converter<ItemType, dev.simplix.protocolize.data.ItemType> {

	@Override
	public dev.simplix.protocolize.data.ItemType convert(final ItemType src) {
		return dev.simplix.protocolize.data.ItemType.valueOf(src.name());
	}

}
