package de.exceptionflug.mccommons.inventories.api;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

/**
 * This class represents a placed item in an {@link InventoryWrapper}. This class holds information about the {@link ActionHandler} to call if the item gets clicked and ist {@link Arguments}.
 * Of course this class holds information about the item itself as an {@link ItemStackWrapper}.
 */
public class InventoryItem {

	private ItemStackWrapper itemStackWrapper;
	private String actionHandler;
	private Arguments arguments;

	public InventoryItem(final ItemStackWrapper itemStackWrapper, final String actionHandler, final Arguments arguments) {
		this.itemStackWrapper = itemStackWrapper;
		this.actionHandler = actionHandler;
		this.arguments = arguments;
	}

	public InventoryItem(final Object otherItemImplConvertible, final String actionHandler, final Arguments arguments) {
		this.itemStackWrapper = Converters.convert(otherItemImplConvertible, ItemStackWrapper.class);
		this.actionHandler = actionHandler;
		this.arguments = arguments;
	}

	public ItemStackWrapper getItemStackWrapper() {
		return itemStackWrapper;
	}

	public void setItemStackWrapper(final ItemStackWrapper itemStackWrapper) {
		this.itemStackWrapper = itemStackWrapper;
	}

	public String getActionHandler() {
		return actionHandler;
	}

	public void setActionHandler(final String actionHandler) {
		this.actionHandler = actionHandler;
	}

	public Arguments getArguments() {
		return arguments;
	}

	public void setArguments(final Arguments arguments) {
		this.arguments = arguments;
	}
}
