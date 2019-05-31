package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.item.ItemIDMapping;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import org.bukkit.Bukkit;
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
        if (src == null) {
            return null;
        }
        final ItemIDMapping applicableMapping = src.getApplicableMapping(Providers.get(ServerVersionProvider.class).getProtocolVersion());
        if (applicableMapping == null) {
            return null;
        }
        Material type = byId(applicableMapping.getId(), src);
        return new MaterialData(type, (byte) applicableMapping.getData());
    }

    public static Material byId(final int id, final ItemType type) {
        for (final Material material : Material.values()) {
            try {
                final int i = (int) idField.get(material);
                if (i == id) {
                    return material;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Bukkit.getLogger().warning("[MCCommons] Since spigot's internal id numbers are wrong, we only can guess the correct id by using its proto name...");
        try {
            return Material.valueOf(type.name());
        } catch (final Exception e) {
            return null;
        }
    }

}
