package de.exceptionflug.mccommons.inventories.api;

import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

public class DataInventoryItem<T> extends InventoryItem {

    private final T data;

    public DataInventoryItem(final ItemStackWrapper itemStack, final String actionHandler, final Arguments arguments, final T data) {
        super(itemStack, actionHandler, arguments);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
