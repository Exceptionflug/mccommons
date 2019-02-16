package de.exceptionflug.mccommons.inventories.api;

import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * This interface describes the base methods for every implementation of {@link InventoryWrapper}.
 * @param <P> Player type
 * @param <I> ItemStack type
 * @param <INV> Inventory type
 */
public interface InventoryWrapper<P, I, INV> {

    /**
     * @return internal id used for comparison
     */
    int getInternalId();

    /**
     * Called when hard coded items should be placed into the inventory.
     */
    void updateInventory();

    /**
     * This will resend the inventory to the player. This will call the build method of the inventory.
     */
    void reopen();

    /**
     * @return The inventory type
     */
    InventoryType getInventoryType();

    /**
     * Changes the inventory type
     * @param type the new type
     */
    void setInventoryType(final InventoryType type);

    /**
     * Builds the platform dependent inventory handle.
     * @return the handle
     */
    INV build();

    /**
     * Registers a new action handler
     * @param name the name
     * @param actionHandler the action handler
     */
    void registerActionHandler(final String name, final ActionHandler actionHandler);

    /**
     * ActionHandler which handles clicks in empty space
     * @param actionHandler the action handler
     */
    void setCustomActionHandler(final ActionHandler actionHandler);

    /**
     * ActionHandler which handles clicks in empty space
     * @return The custom action handler
     */
    ActionHandler getCustomActionHandler();

    /**
     * Returns an action handler by its name
     * @param name the name of the desired action handler
     * @return the action handler or null if not found
     */
    ActionHandler getActionHandler(final String name);

    /**
     * Sets an item into the inventory at a specific slot.
     * @param slot The slot where the item should go
     * @param stack The stack to set
     * @param actionHandler The action handler to handle clicks
     * @param args Optional arguments
     */
    void set(final int slot, final I stack, final String actionHandler, final Arguments args);

    /**
     * Returns a {@link InventoryItem} of a specific slot.
     * @param slot the slot
     * @return the inventory item or null if slot is empty
     */
    InventoryItem get(final int slot);

    /**
     * Returns the raw contents of the inventory
     * @return the map
     */
    Map<Integer, InventoryItem> getInventoryItemMap();

    /**
     * @return the player which owns this wrapper instance
     */
    P getPlayer();

    /**
     * @return The specified size of the inventory
     */
    int getSize();

    /**
     * @return the slot index of the next available empty slot or -1 if no slot is empty
     */
    int getNextFreeSlot();

    /**
     * @return The title displayed at the top of the inventory
     */
    String getTitle();

    /**
     * @return The locale the inventory is in
     */
    Locale getLocale();

    /**
     * Sets a default placeholder replacer which will be used for configurable items
     * @param replacer the replacer
     */
    void setDefaultReplacer(final Supplier<String[]> replacer);

    /**
     * Sets the size of the current inventory. This will do nothing if the inventory is not a chest inventory.
     * @param size the new size
     */
    void setSize(final int size);

    /**
     * Sets the title of the inventory
     * @param title the title
     */
    void setTitle(final String title);

    /**
     * Completely clears the inventory contents
     */
    void clear();

    /**
     * Executes unsafe code that may throw a exception. When an exception is thrown, it will be delegated to the wrapper
     * exception handler.
     *
     * @param runnable The runnable to run
     */
    default void unsafe(final SafeRunnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            onException(e, null);
        }
    }

    /**
     * Called whenever an exception occurs during action handling or unsafe code execution.
     * @param exception The thrown exception
     * @param inventoryItem The currently clicked item or null
     */
    void onException(final Exception exception, final InventoryItem inventoryItem);

    /**
     * Called when leaving the current wrapper inventory.
     * @param inventorySwitch true if a new wrapped inventory was built within the last 55ms
     */
    void onExit(final boolean inventorySwitch);

    /**
     * Adds a new item to the inventory at the fist empty slot.
     * @param stack The stack to set
     * @param actionHandler The action handler
     * @param args The arguments
     */
    default void add(final I stack, final String actionHandler, final Arguments args) {
        set(getNextFreeSlot(), stack, actionHandler, args);
    }

    /**
     * Adds a new item to the inventory at the fist empty slot.
     * @param stack The stack to set
     * @param actionHandler The action handler
     */
    default void add(final I stack, final String actionHandler) {
        add(stack, actionHandler, Arguments.empty());
    }

    /**
     * Adds a new item to the inventory at the fist empty slot.
     * @param stack The stack to set
     * @param actionHandler The action handler
     * @param args The arguments
     */
    default void add(final ConfigItemStack stack, final String actionHandler, final Arguments args) {
        add((I) Converters.convert(stack, ItemStackWrapper.class).getHandle(), actionHandler, args);
    }

    /**
     * Adds a new item to the inventory at the fist empty slot.
     * @param stack The stack to set
     * @param actionHandler The action handler
     */
    default void add(final ConfigItemStack stack, final String actionHandler) {
        add((I) Converters.convert(stack, ItemStackWrapper.class).getHandle(), actionHandler, Arguments.empty());
    }

    /**
     * Sets an item into the inventory at a specific slot.
     * @param slot The slot where the item should go
     * @param stack The stack to set
     * @param actionHandler The action handler to handle clicks
     */
    default void set(final int slot, final I stack, final String actionHandler) {
        set(slot, stack, actionHandler, Arguments.empty());
    }

    /**
     * Sets an item into the inventory at a specific slot.
     * @param slot The slot where the item should go
     * @param stack The stack to set
     * @param actionHandler The action handler to handle clicks
     * @param args Optional arguments
     */
    default void set(final int slot, final ConfigItemStack stack, final String actionHandler, final Arguments args) {
        set(slot, (I) Converters.convert(stack, ItemStackWrapper.class).getHandle(), actionHandler, args);
    }

    /**
     * Sets an item into the inventory at a specific slot.
     * @param slot The slot where the item should go
     * @param stack The stack to set
     * @param actionHandler The action handler to handle clicks
     */
    default void set(final int slot, final ConfigItemStack stack, final String actionHandler) {
        set(slot, (I) Converters.convert(stack, ItemStackWrapper.class).getHandle(), actionHandler);
    }

}
