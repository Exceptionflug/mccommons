package de.exceptionflug.mccommons.inventories.api;

/**
 * {@link ActionHandler}s are functions that get called when a click on an {@link InventoryItem} occurs.
 */
public interface ActionHandler {

    CallResult handle(final Click click);

}
