package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.item.IDMapping;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import org.bukkit.material.MaterialData;

public class ItemTypeMaterialDataConverter implements Converter<ItemType, MaterialData> {

    @Override
    public MaterialData convert(final ItemType src) {
        if(src == null)
            return null;
        final IDMapping applicableMapping = src.getApplicableMapping(Providers.get(ServerVersionProvider.class).getProtocolVersion());
        if(applicableMapping == null) {
            return null;
        }
        return new MaterialData(applicableMapping.getId(), (byte) applicableMapping.getData());
    }

}
