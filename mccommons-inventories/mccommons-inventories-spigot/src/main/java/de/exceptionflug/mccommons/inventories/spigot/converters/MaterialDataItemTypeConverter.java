package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import org.bukkit.material.MaterialData;

public class MaterialDataItemTypeConverter implements Converter<MaterialData, ItemType> {

    @Override
    public ItemType convert(final MaterialData src) {
        return ItemType.getType(ItemTypeMaterialDataConverter.getId(src.getItemType()), src.getData(), Providers.get(ServerVersionProvider.class).getProtocolVersion(), null); // This will throw NPE when converting SpawnEggs
    }

}
