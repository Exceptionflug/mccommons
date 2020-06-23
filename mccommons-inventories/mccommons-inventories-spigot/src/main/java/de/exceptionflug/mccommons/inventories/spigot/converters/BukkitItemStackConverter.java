package de.exceptionflug.mccommons.inventories.spigot.converters;

import com.flowpowered.nbt.CompoundTag;
import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;
import de.exceptionflug.protocolize.items.ItemType;
import org.bukkit.inventory.ItemStack;

public class BukkitItemStackConverter implements Converter<ItemStack, de.exceptionflug.protocolize.items.ItemStack> {

    private static Class<?> craftItemStackClass;
    private static Class<?> itemStackNMSClass;

    static {
        try {
            craftItemStackClass = ReflectionUtil.getClass("{obc}.inventory.CraftItemStack");
            itemStackNMSClass = ReflectionUtil.getClass("{nms}.ItemStack");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public de.exceptionflug.protocolize.items.ItemStack convert(ItemStack src) {
        try {
            de.exceptionflug.protocolize.items.ItemStack out = new de.exceptionflug.protocolize.items.ItemStack(Converters.convert(src.getData(), ItemType.class), src.getAmount(), src.getDurability());
            Object handle = ReflectionUtil.getFieldValue(craftItemStackClass, src, "handle");
            out.setNBTTag(Converters.convert(itemStackNMSClass.getMethod("getTag").invoke(handle), CompoundTag.class));
            return out;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

}
