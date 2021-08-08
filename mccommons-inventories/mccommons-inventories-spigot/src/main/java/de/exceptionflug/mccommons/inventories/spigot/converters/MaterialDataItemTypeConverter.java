package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.utils.ProtocolVersions;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import org.bukkit.material.MaterialData;

public class MaterialDataItemTypeConverter implements Converter<MaterialData, ItemType> {

	@Override
	public ItemType convert(final MaterialData src) {
		if (Providers.get(ServerVersionProvider.class).getProtocolVersion() >= ProtocolVersions.MINECRAFT_1_14) {
			return ItemType.valueOf(src.getItemType().name());
		}
		return null;
	}

}
