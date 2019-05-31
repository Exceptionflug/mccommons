package de.exceptionflug.mccommons.inventories.spigot.item;

import com.flowpowered.nbt.CompoundTag;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.item.AbstractCustomItemIDMapping;
import de.exceptionflug.mccommons.inventories.api.item.ItemIDMapping;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.converters.ItemTypeMaterialDataConverter;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpigotItemStackWrapper implements ItemStackWrapper {

    private final ItemStack handle;

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
        return ItemType.getType(ItemTypeMaterialDataConverter.getId(handle.getType()), handle.getDurability(), Providers.get(ServerVersionProvider.class).getProtocolVersion(), this);
    }

    @Override
    public CompoundTag getNBT() {
        try {
            return Converters.convert(((net.minecraft.server.v1_14_R1.ItemStack)ReflectionUtil.getFieldValue(CraftItemStack.class, handle, "handle")).getTag(), CompoundTag.class);
        } catch (final IllegalAccessException | NoSuchFieldException e) {
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
        handle.setType(ItemTypeMaterialDataConverter.byId(applicableMapping.getId(), type));
        handle.setDurability((short) applicableMapping.getData());
        if(applicableMapping instanceof AbstractCustomItemIDMapping)
            ((AbstractCustomItemIDMapping) applicableMapping).apply(this, Providers.get(ServerVersionProvider.class).getProtocolVersion());
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
            ((net.minecraft.server.v1_14_R1.ItemStack)ReflectionUtil.getFieldValue(CraftItemStack.class, handle, "handle")).setTag(Converters.convert(tag, NBTTagCompound.class));
        } catch (final IllegalAccessException | NoSuchFieldException e) {
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
