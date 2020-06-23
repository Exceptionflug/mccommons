package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;
import de.exceptionflug.protocolize.items.ItemStack;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Method;

public class ProtocolizeItemStackConverter implements Converter<ItemStack, org.bukkit.inventory.ItemStack> {

    private static Class<?> craftItemStackClass;
    private static Class<?> nbtTagCompoundClass;
    private static Class<?> itemStackNMSClass;

    static {
        try {
            craftItemStackClass = ReflectionUtil.getClass("{obc}.inventory.CraftItemStack");
            nbtTagCompoundClass = ReflectionUtil.getClass("{nms}.NBTTagCompound");
            itemStackNMSClass = ReflectionUtil.getClass("{nms}.ItemStack");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public org.bukkit.inventory.ItemStack convert(ItemStack src) {
        MaterialData data = Converters.convert(src.getType(), MaterialData.class);
        org.bukkit.inventory.ItemStack out = new org.bukkit.inventory.ItemStack(data.getItemType(), src.getAmount(), src.getDurability(), data.getData());
        try {
            Object handle = ReflectionUtil.getFieldValue(craftItemStackClass, out, "handle");
            Method setTag = itemStackNMSClass.getMethod("setTag", nbtTagCompoundClass);
            setTag.invoke(handle, Converters.convert(src.getNBTTag(), nbtTagCompoundClass));
        } catch (final Exception e) {
            e.printStackTrace(); // Setting nbt to nms item is also pain in the ass
        }
        return out;
    }

}
