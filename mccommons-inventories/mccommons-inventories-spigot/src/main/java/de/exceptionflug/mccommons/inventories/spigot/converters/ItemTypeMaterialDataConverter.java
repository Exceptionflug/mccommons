package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.item.ItemIDMapping;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;

public class ItemTypeMaterialDataConverter implements Converter<ItemType, MaterialData> {

    private static Field idField;

    static {
        try {
            idField = Material.class.getDeclaredField("id");
            idField.setAccessible(true);
        } catch (Exception e) {
        }
    }

    public static int getId(Material type) {
        try {
            return (int) idField.get(type);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public MaterialData convert(final ItemType src) {
        if(src == null)
            return null;
        final ItemIDMapping applicableMapping = src.getApplicableMapping(Providers.get(ServerVersionProvider.class).getProtocolVersion());
        if(applicableMapping == null) {
            return null;
        }
        return new MaterialData(byId(applicableMapping.getId()), (byte) applicableMapping.getData());
    }

    public static Material byId(final int id) {
        for(final Material material : Material.values()) {
            if(!material.isLegacy()) {
                try {
                    if((int) idField.get(material) == id) {
                        return material;
                    }
                } catch (IllegalAccessException e) {
                }
            }
        }
        return null;
    }

}
