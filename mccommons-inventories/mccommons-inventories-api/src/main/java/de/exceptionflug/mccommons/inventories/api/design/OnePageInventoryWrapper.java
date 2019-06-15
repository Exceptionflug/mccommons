package de.exceptionflug.mccommons.inventories.api.design;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.inventories.api.*;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple one page inventory. This implementation is the simplest of an {@link InventoryWrapper}. This inventory can hold {@link InventoryItem}s
 * which can call {@link ActionHandler}s. You can configure items by configuration file and set there action handler.
 * @param <P>
 * @param <I>
 * @param <INV>
 */
public class OnePageInventoryWrapper<P, I, INV> extends AbstractBaseInventoryWrapper<P, I, INV> {

    private final ConfigWrapper config;
    private boolean customTitle;
    private ItemStackWrapper placeHolder;

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper) {
        this(player, configWrapper.isSet("Inventory.size") ? InventoryType.getChestInventoryWithSize(configWrapper.getOrSetDefault("Inventory.size", 36)) : InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "GENERIC_9X5")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true);
    }

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, configWrapper.isSet("Inventory.size") ? InventoryType.getChestInventoryWithSize(configWrapper.getOrSetDefault("Inventory.size", 36)) : InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "GENERIC_9X5")), configWrapper, locale, true);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, type, configWrapper, locale, true);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final boolean update) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update);
    }

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final boolean update) {
        this(player, configWrapper.isSet("Inventory.size") ? InventoryType.getChestInventoryWithSize(configWrapper.getOrSetDefault("Inventory.size", 36)) : InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "GENERIC_9X5")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update);
    }

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final Locale locale, final boolean update) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "GENERIC_9X5")), configWrapper, locale, update);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final Locale locale, final boolean update) {
        super(player, type, locale);
        this.config = configWrapper;
        setTitle(configWrapper.getLocalizedString(locale, "Inventory", ".title", "&6Inventory"));
        registerActionHandler("noAction", click -> CallResult.DENY_GRABBING);
        registerActionHandlers();
        if(update)
            updateInventory();
    }

    @Override
    public void updateInventory() {
        if(!customTitle) {
            setTitle(FormatUtils.format(config.getLocalizedString(getLocale(), "Inventory", ".title", "&6Inventory"), replacer));
            customTitle = false;
        }
        if(placeHolder == null)
            placeHolder = Converters.convert(config.getItemStack("Placeholder.itemStack", replacer), ItemStackWrapper.class);
        final List<Integer> placeholderSlots = config.getOrSetDefault("Placeholder.slots", new ArrayList<>());
        for(final int slot : placeholderSlots) {
            set(slot, (I) placeHolder.getHandle(), "noAction");
        }
        for(final String key : config.getKeys("Items")) {
            final int slot = config.getOrSetDefault("Items."+key+".slot", 0);
            final String actionHandler = config.getOrSetDefault("Items."+key+".actionHandler", "noAction");
            final List<Object> args = config.getOrSetDefault("Items."+key+".actionArguments", new ArrayList<>());
            set(slot, config.getItemStack("Items."+key+".itemStack", getLocale(), replacer), actionHandler, new Arguments(args));
        }
    }

    @Override
    public void setInventoryType(InventoryType type) {
        super.setInventoryType(type);
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);
        customTitle = true;
    }

    @Override
    public void onExit(final boolean inventorySwitch) {
    }

    public void setPlaceHolder(final ItemStackWrapper placeHolder) {
        this.placeHolder = placeHolder;
    }

}
