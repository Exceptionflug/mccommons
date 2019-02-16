package de.exceptionflug.mccommons.inventories.proxy.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;

public class ItemTypeConverter implements Converter<ItemType, de.exceptionflug.protocolize.items.ItemType> {

    @Override
    public de.exceptionflug.protocolize.items.ItemType convert(final ItemType src) {
        return de.exceptionflug.protocolize.items.ItemType.valueOf(src.name());
    }

}
