package de.exceptionflug.mccommons.inventories.proxy.item;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.protocolize.items.ItemStack;
import net.querz.nbt.tag.CompoundTag;

import java.util.List;

public class ProtocolizeItemStackWrapper implements ItemStackWrapper {

	private final ItemStack handle;

	public ProtocolizeItemStackWrapper(final ItemStack handle) {
		this.handle = handle;
	}

	@Override
	public String getDisplayName() {
		return handle.getDisplayName();
	}

	@Override
	public List<String> getLore() {
		return handle.getLore();
	}

	@Override
	public ItemType getType() {
		return Converters.convert(handle.getType(), ItemType.class);
	}

	@Override
	public CompoundTag getNBT() {
		return (CompoundTag) handle.getNBTTag();
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
		handle.setType(Converters.convert(type, de.exceptionflug.protocolize.items.ItemType.class));
	}

	@Override
	public void setDisplayName(final String s) {
		handle.setDisplayName(s);
	}

	@Override
	public void setLore(final List<String> lore) {
		handle.setLore(lore);
	}

	@Override
	public void setNBT(final CompoundTag tag) {
		handle.setNBTTag(tag);
	}

	@Override
	public void setAmount(final int amount) {
		handle.setAmount((byte) amount);
	}

	@Override
	public void setDurability(final short durability) {
		handle.setDurability(durability);
	}

	@Override
	public <T> T getHandle() {
		return (T) handle;
	}
}
