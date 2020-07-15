package de.exceptionflug.mccommons.inventories.proxy.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.api.ClickType;

public class ProtocolizeClickTypeConverter implements Converter<de.exceptionflug.protocolize.api.ClickType, ClickType> {

	@Override
	public ClickType convert(de.exceptionflug.protocolize.api.ClickType src) {
		return ClickType.valueOf(src.name());
	}

}
