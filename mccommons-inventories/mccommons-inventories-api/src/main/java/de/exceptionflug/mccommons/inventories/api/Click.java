package de.exceptionflug.mccommons.inventories.api;

import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

public class Click {

    private final ClickType clickType;
    private final InventoryWrapper clickedInventory;
    private final ItemStackWrapper clickedItem;
    private final int slot;

    public Click(final ClickType clickType, final InventoryWrapper clickedInventory, final ItemStackWrapper clickedItem, final int slot) {
        this.clickType = clickType;
        this.clickedInventory = clickedInventory;
        this.clickedItem = clickedItem;
        this.slot = slot;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public InventoryWrapper getClickedInventory() {
        return clickedInventory;
    }

    public ItemStackWrapper getClickedItem() {
        return clickedItem;
    }

    public int getSlot() {
        return slot;
    }
}
