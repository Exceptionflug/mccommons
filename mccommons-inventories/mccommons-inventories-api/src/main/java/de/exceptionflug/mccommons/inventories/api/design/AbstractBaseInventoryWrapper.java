package de.exceptionflug.mccommons.inventories.api.design;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.inventories.api.*;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Basic implementation of an {@link InventoryWrapper} which holds {@link InventoryItem}s bound to a slot. This class also holds the {@link ActionHandler}s registered by the implementation. This class is the basis of all other sub-implementations of {@link InventoryWrapper}.
 */
public abstract class AbstractBaseInventoryWrapper<P, I, INV> implements InventoryWrapper<P, I, INV> {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger();

    private final Map<String, ActionHandler> actionHandlerMap = new HashMap<>();
    private final Map<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
    protected P player;
    protected final Locale locale;
    private final int id = ID_COUNTER.incrementAndGet();
    protected Supplier<String[]> replacer = () -> new String[0];
    private InventoryType type;
    private ActionHandler customActionHandler;
    private String title;
    Object inventory;

    protected AbstractBaseInventoryWrapper(final P player, final InventoryType type, final Locale locale) {
        this.player = player;
        this.type = type;
        this.locale = locale;
    }

    @Override
    public void set(final int slot, final I stack, final String actionHandler, final Arguments args) {
        if(stack == null) {
            inventoryItemMap.remove(slot);
            return;
        }
        inventoryItemMap.put(slot, new InventoryItem(Converters.convert(stack, ItemStackWrapper.class), actionHandler, args));
    }

    public int getNextFreeSlot() {
        for (int i = 0; i < getSize(); i++) {
            final InventoryItem item = get(i);
            if(item == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public InventoryItem get(final int slot) {
        return inventoryItemMap.get(slot);
    }

    @Override
    public void registerActionHandler(final String name, final ActionHandler actionHandler) {
        actionHandlerMap.put(name, actionHandler);
    }

    @Override
    public ActionHandler getActionHandler(final String name) {
        return actionHandlerMap.get(name);
    }

    public void registerActionHandlers() {
    }

    @Override
    public void onException(final Exception exception, final InventoryItem inventoryItem) {
    }

    @Override
    public INV build() {
        final Object build = Providers.get(InventoryBuilder.class).build(inventory, this);
        inventory = build;
        return (INV) build;
    }

    @Override
    public void reopen() {
        Providers.get(InventoryBuilder.class).open(this);
    }

    @Override
    public void clear() {
        inventoryItemMap.clear();
    }

    public int getSize() {
        return type.getTypicalSize(Converters.convert(player, PlayerWrapper.class).getProtocolVersion());
    }

    public void setTitle(final String title) {
        this.title = FormatUtils.formatAmpersandColorCodes(title);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setDefaultReplacer(final Supplier<String[]> replacer) {
        this.replacer = replacer;
    }

    @Override
    public InventoryType getInventoryType() {
        return type;
    }

    @Override
    public void setInventoryType(final InventoryType type) {
        this.type = type;
    }

    @Override
    public Map<Integer, InventoryItem> getInventoryItemMap() {
        return inventoryItemMap;
    }

    @Override
    public P getPlayer() {
        return player;
    }

    @Override
    public boolean equals(final Object obj) {
        if(inventory != null && obj.getClass().isAssignableFrom(inventory.getClass())) {
            return inventory.equals(obj);
        }
        return super.equals(obj);
    }

    @Override
    public void markAsUnregistered() {
        inventory = null;
    }

    @Override
    public ActionHandler getCustomActionHandler() {
        return customActionHandler;
    }

    @Override
    public void setCustomActionHandler(final ActionHandler customActionHandler) {
        this.customActionHandler = customActionHandler;
    }

    @Override
    public int getInternalId() {
        return id;
    }
}
