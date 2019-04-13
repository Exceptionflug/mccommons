package de.exceptionflug.mccommons.inventories.api.design;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.AsyncProvider;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.inventories.api.CallResult;
import de.exceptionflug.mccommons.inventories.api.DataInventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractAsyncFetchMultiPageInventoryWrapper<P, I, INV, T> extends MultiPageInventoryWrapper<P, I, INV> {

    private final boolean showLoadingScreen;
    private boolean fetched;
    private int fetchIndex;
    private boolean cancelled;

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true, true);
    }

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final ConfigWrapper configWrapper) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "CHEST")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true, true);
    }

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, type, configWrapper, locale, true, true);
    }

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "CHEST")), configWrapper, locale, true, true);
    }

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final boolean update, final boolean loading) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update, loading);
    }

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final boolean update, final boolean loading) {
        this(player, InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "CHEST")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update, loading);
    }

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final ConfigWrapper config, final Locale locale, final boolean update, final boolean loading) {
        this(player, InventoryType.valueOf(config.getOrSetDefault("Inventory.type", "CHEST")), config, locale, update, loading);
    }

    protected AbstractAsyncFetchMultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper config, final Locale locale, final boolean update, final boolean loading) {
        super(player, type, config, locale, update);

        registerActionHandler("nextPage", click -> {
            if(getPages().size() == getCurrentPageIndex())
                return CallResult.DENY_GRABBING;
            Providers.get(AsyncProvider.class).async(() -> unsafe(() -> {
                final int currentPage = getCurrentPageIndex();
                fetch().forEach(this::addFetchedItem);
                setCurrentPage(currentPage + 1);
                updateInventory();
                build();
            }));
            return CallResult.DENY_GRABBING;
        });

        this.showLoadingScreen = loading;
        refetch();
    }

    public abstract void async0(final Runnable runnable);
    public abstract void sync0(final Runnable runnable);

    public void refetch() {
        fetched = false;
        async0(() -> unsafe(() -> {
            fetch().forEach(this::addFetchedItem);
            fetched = true;
        }));
        if(showLoadingScreen) {
            final AtomicInteger frameIndex = new AtomicInteger();
            final List<String> titleAnimation = config.getLocalizedStringList(getLocale(), "Inventory", ".loadingTitleAnimation", Arrays.asList("&8Loading |", "&8Loading /", "&8Loading -", "&8Loading \\"));
            setTitle(titleAnimation.get(0));
            async0(() -> unsafe(() -> {
                while (!fetched && !cancelled) {
                    int nextIndex = frameIndex.incrementAndGet();
                    if(nextIndex == titleAnimation.size()) {
                        frameIndex.set(0);
                        nextIndex = 0;
                    }
                    setTitle(titleAnimation.get(nextIndex));
                    sync0(this::build);
                    Thread.sleep(300);
                }
                if(!cancelled) {
                    setCustomTitle(false);
                    sync0(this::buildFirstPage);
                }
            }));
        }
    }

    public boolean isFetched() {
        return fetched;
    }

    private void addFetchedItem(final DataInventoryItem<T> item) {
        final int slot = getNextFreeSlot();
        if(slot == -1) {
            if(getPages().size() > getCurrentPageIndex()) {
                setCurrentPage(getPages().size());
                addFetchedItem(item);
                return;
            }
            newPage();
            addFetchedItem(item);
            return;
        }
        getInventoryItemMap().put(slot, item);
    }

    public abstract List<DataInventoryItem<T>> fetch(final int start, final int end) throws Exception;

    private List<DataInventoryItem<T>> fetch() throws Exception {
        final int start = fetchIndex;
        final List<DataInventoryItem<T>> fetch = fetch(start, fetchIndex + itemsPerPage);
        fetchIndex += fetch.size();
        return fetch;
    }

    @Override
    public void onExit(final boolean inventorySwitch) {
        cancelled = true;
    }
}
