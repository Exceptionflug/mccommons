package de.exceptionflug.mccommons.inventories.api.item;

import com.flowpowered.nbt.CompoundTag;

import java.util.List;

public interface ItemStackWrapper {

    String getDisplayName();
    List<String> getLore();
    ItemType getType();
    CompoundTag getNBT();
    int getAmount();
    short getDurability();

    void setType(final ItemType type);
    void setDisplayName(final String s);
    void setLore(final List<String> lore);
    void setNBT(final CompoundTag tag);
    void setAmount(final int amount);
    void setDurability(final short durability);

    <T> T getHandle();

}
