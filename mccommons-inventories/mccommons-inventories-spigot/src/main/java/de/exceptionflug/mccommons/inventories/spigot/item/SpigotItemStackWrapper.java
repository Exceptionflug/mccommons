package de.exceptionflug.mccommons.inventories.spigot.item;

import com.flowpowered.nbt.CompoundTag;
import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import de.exceptionflug.protocolize.items.AbstractCustomItemIDMapping;
import de.exceptionflug.protocolize.items.ItemIDMapping;
import de.exceptionflug.protocolize.items.ItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.List;

public class SpigotItemStackWrapper implements ItemStackWrapper {

    private ItemStack handle;
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

    public SpigotItemStackWrapper(final ItemStack handle) {
        this.handle = handle;
    }

    @Override
    public String getDisplayName() {
        return handle.getItemMeta().getDisplayName();
    }

    @Override
    public List<String> getLore() {
        return handle.getItemMeta().getLore();
    }

    @Override
    public ItemType getType() {
        return ItemType.getType(handle.getTypeId(), handle.getDurability(), Providers.get(ServerVersionProvider.class).getProtocolVersion(), null);
    }

    @Override
    public CompoundTag getNBT() {
        try {
            Object handle = ReflectionUtil.getFieldValue(craftItemStackClass, this.handle, "handle");
            return Converters.convert(itemStackNMSClass.getMethod("getTag").invoke(handle), CompoundTag.class);
        } catch (final Exception e) {
            e.printStackTrace(); // Getting nbt from nms item is pain in the ass
        }
        return null;
    }

    @Override
    public int getAmount() {
        return handle.getAmount();
    }

    @Override
    public short getDurability() {
        return handle.getDurability();
    }

    @Override
    public void setType(final ItemType type) {
        final ItemIDMapping applicableMapping = type.getApplicableMapping(Providers.get(ServerVersionProvider.class).getProtocolVersion());
        if(applicableMapping == null)
            throw new UnsupportedOperationException(type.name()+" is not allowed on version "+ReflectionUtil.getVersion());
        handle.setType(Material.getMaterial(applicableMapping.getId()));
        handle.setDurability((short) applicableMapping.getData());
        if(applicableMapping instanceof AbstractCustomItemIDMapping) {
            de.exceptionflug.protocolize.items.ItemStack convert = Converters.convert(handle, de.exceptionflug.protocolize.items.ItemStack.class);
            ((AbstractCustomItemIDMapping) applicableMapping).apply(convert, Providers.get(ServerVersionProvider.class).getProtocolVersion());
            handle = Converters.convert(convert, ItemStack.class);
        }
    }

    @Override
    public void setDisplayName(final String s) {
        final ItemMeta meta = handle.getItemMeta();
        meta.setDisplayName(s);
        handle.setItemMeta(meta);
    }

    @Override
    public void setLore(final List<String> lore) {
        final ItemMeta meta = handle.getItemMeta();
        meta.setLore(lore);
        handle.setItemMeta(meta);
    }

    @Override
    public void setNBT(final CompoundTag tag) {
        try {
            Object handle = ReflectionUtil.getFieldValue(craftItemStackClass, this.handle, "handle");
            Method setTag = itemStackNMSClass.getMethod("setTag", nbtTagCompoundClass);
            setTag.invoke(handle, Converters.convert(tag, nbtTagCompoundClass));
        } catch (final Exception e) {
            e.printStackTrace(); // Setting nbt to nms item is also pain in the ass
        }
    }

    @Override
    public void setAmount(final int amount) {
        handle.setAmount(amount);
    }

    @Override
    public void setDurability(short durability) {
        handle.setDurability(durability);
    }

    @Override
    public <T> T getHandle() {
        return (T) handle;
    }
}
