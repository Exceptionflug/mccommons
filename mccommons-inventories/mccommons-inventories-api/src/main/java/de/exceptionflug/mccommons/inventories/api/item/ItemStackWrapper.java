package de.exceptionflug.mccommons.inventories.api.item;

import com.flowpowered.nbt.CompoundTag;
import de.exceptionflug.protocolize.items.ItemType;

import java.util.List;

/**
 * An {@link ItemStackWrapper} provides uniform data access for items across different platforms. Please be aware, that most of the given
 * properties are immutable, so you have to set them using their setter after you have done some changes.
 */
public interface ItemStackWrapper {

    /**
     * Returns the display name of an item.
     * @return display name
     */
    String getDisplayName();

    /**
     * The lore of the item.
     * @return lore
     */
    List<String> getLore();

    /**
     * The mcc {@link ItemType} of the item.
     * @return item type
     */
    ItemType getType();

    /**
     * This returns a flow-nbt {@link CompoundTag} with the nbt information about this item. Please be aware, that the
     * nbt data of spigot ItemStacks are immutable. So you have to call setNBT() after you changed the data to set them.
     * @return nbt data
     */
    CompoundTag getNBT();

    /**
     * Returns the amount of the stack
     * @return amount
     */
    int getAmount();

    /**
     * Returns the durability of the item.
     * @return
     */
    short getDurability();

    /**
     * Sets the mcc item type.
     * @param type type to set
     */
    void setType(final ItemType type);

    /**
     * Sets the display name of the item
     * @param s name
     */
    void setDisplayName(final String s);

    /**
     * Sets the lore of the item
     * @param lore
     */
    void setLore(final List<String> lore);

    /**
     * Sets the nbt data.
     * @param tag nbt data
     */
    void setNBT(final CompoundTag tag);

    /**
     * Sets the stack amount.
     * @param amount amount
     */
    void setAmount(final int amount);

    /**
     * Sets the durability.
     * <br>#DUDELAVERITY
     * @param durability durability
     */
    void setDurability(final short durability);

    /**
     * Returns the handle of the wrapper
     * @param <T> dyncast handle type
     * @return the handle
     */
    <T> T getHandle();

}
