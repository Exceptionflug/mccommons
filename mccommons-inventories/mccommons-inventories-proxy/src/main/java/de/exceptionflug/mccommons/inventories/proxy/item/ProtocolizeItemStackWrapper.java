package de.exceptionflug.mccommons.inventories.proxy.item;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import dev.simplix.protocolize.api.item.ItemStack;
import net.querz.nbt.tag.CompoundTag;

import java.util.List;

public class ProtocolizeItemStackWrapper implements ItemStackWrapper {

	private final ItemStack handle;

	public ProtocolizeItemStackWrapper(final ItemStack handle) {
		this.handle = handle;
	}

	@Override
	public String getDisplayName() {
		return handle.displayName(true);
	}

	@Override
	public List<String> getLore() {
		return handle.lore(true);
	}

	@Override
	public ItemType getType() {
		return Converters.convert(handle.itemType(), ItemType.class);
	}

	@Override
	public CompoundTag getNBT() {
		return (CompoundTag) handle.nbtData();
	}

	@Override
	public int getAmount() {
		return handle.amount();
	}

	@Override
	public short getDurability() {
		return handle.durability();
	}

	@Override
	public void setType(final ItemType type) {
		handle.itemType(Converters.convert(type, dev.simplix.protocolize.data.ItemType.class));
	}

	@Override
	public void setDisplayName(final String s) {
		handle.displayName(s);
	}

	@Override
	public void setLore(final List<String> lore) {
		handle.lore(lore, true);
	}

	@Override
	public void setNBT(final CompoundTag tag) {
		handle.nbtData(tag);
	}

	@Override
	public void setAmount(final int amount) {
		handle.amount((byte) amount);
	}

	@Override
	public void setDurability(final short durability) {
		handle.durability(durability);
	}

	@Override
	public <T> T getHandle() {
		return (T) handle;
	}
}
