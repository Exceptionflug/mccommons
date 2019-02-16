package de.exceptionflug.mccommons.inventories.api;

import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

public class InventoryItem {

    private ItemStackWrapper itemStackWrapper;
    private String actionHandler;
    private Arguments arguments;

    public InventoryItem(final ItemStackWrapper itemStackWrapper, final String actionHandler, final Arguments arguments) {
        this.itemStackWrapper = itemStackWrapper;
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
