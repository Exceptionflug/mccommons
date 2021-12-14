package de.exceptionflug.mccommons.inventories.proxy.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.api.ClickType;

public class ProtocolizeClickTypeConverter implements Converter<dev.simplix.protocolize.api.ClickType, ClickType> {

	@Override
	public ClickType convert(dev.simplix.protocolize.api.ClickType src) {
		return ClickType.valueOf(src.name());
	}

}
