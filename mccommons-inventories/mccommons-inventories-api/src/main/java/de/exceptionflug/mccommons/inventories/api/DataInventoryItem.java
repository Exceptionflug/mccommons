package de.exceptionflug.mccommons.inventories.api;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.design.AbstractAsyncFetchMultiPageInventoryWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

/**
 * A data inventory item is a {@link InventoryItem} which additionally maps an instance of T to the item. This is used by {@link AbstractAsyncFetchMultiPageInventoryWrapper}.
 *
 * @param <T> data type
 * @see InventoryItem
 */
public class DataInventoryItem<T> extends InventoryItem {

	private final T data;

	public DataInventoryItem(final ItemStackWrapper itemStack, final String actionHandler, final Arguments arguments, final T data) {
		super(itemStack, actionHandler, arguments);
		this.data = data;
	}

	public DataInventoryItem(final Object otherItemTypeConvertible, final String actionHandler, final Arguments arguments, final T data) {
		super(Converters.convert(otherItemTypeConvertible, ItemStackWrapper.class), actionHandler, arguments);
		this.data = data;
	}

	public T getData() {
		return data;
	}
}
