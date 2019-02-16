package de.exceptionflug.mccommons.inventories.api.design;

import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.inventories.api.Arguments;
import de.exceptionflug.mccommons.inventories.api.CallResult;
import de.exceptionflug.mccommons.inventories.api.InventoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OnePageInventoryWrapper<P, I, INV> extends AbstractBaseInventoryWrapper<P, I, INV> {

    private final ConfigWrapper config;
    private boolean customTitle;

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "CHEST")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true);
    }

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "CHEST")), configWrapper, locale, true);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, type, configWrapper, locale, true);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final boolean update) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update);
    }

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final boolean update) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "CHEST")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update);
    }

    protected OnePageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final Locale locale, final boolean update) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "CHEST")), configWrapper, locale, update);
    }

    protected OnePageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final Locale locale, final boolean update) {
        super(player, type, locale);
        this.config = configWrapper;
        setTitle(configWrapper.getLocalizedString(locale, "Inventory", ".title", "&6Inventory"));
        setSize(configWrapper.getOrSetDefault("Inventory.size", 54));
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
        final ConfigItemStack placeHolder = config.getItemStack("Placeholder.itemStack", replacer);
        final List<Integer> placeholderSlots = config.getOrSetDefault("Placeholder.slots", new ArrayList<>());
        for(final int slot : placeholderSlots) {
            set(slot, placeHolder, "noAction");
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
        setSize(config.getOrSetDefault("Inventory.size", 54));
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);
        customTitle = true;
    }

    @Override
    public void onExit(final boolean inventorySwitch) {
    }
}
