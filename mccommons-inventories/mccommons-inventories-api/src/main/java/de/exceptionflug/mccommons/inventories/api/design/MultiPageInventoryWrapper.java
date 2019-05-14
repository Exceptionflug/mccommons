package de.exceptionflug.mccommons.inventories.api.design;

import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.inventories.api.*;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;

import java.util.*;
import java.util.function.Supplier;

public class MultiPageInventoryWrapper<P, I, INV> extends AbstractBaseInventoryWrapper<P, I, INV> {

    final ConfigWrapper config;
    private final List<ChildInventoryWrapper<P, I, INV>> childs = new LinkedList<>();
    private final List<Integer> nextPageItemSlots, previousPageItemSlots;
    private final ConfigItemStack nextPageItem, previousPageItem;
    private int currentPage = 1;
    int itemsPerPage;
    private boolean customTitle;
    private ItemStackWrapper placeHolder;

    protected MultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true);
    }

    protected MultiPageInventoryWrapper(final P player, final ConfigWrapper configWrapper) {
        this(player, configWrapper.isSet("Inventory.size") ? InventoryType.getChestInventoryWithSize(configWrapper.getOrSetDefault("Inventory.size", 36)) : InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "GENERIC_9X5")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), true);
    }

    protected MultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, type, configWrapper, locale, true);
    }

    protected MultiPageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final Locale locale) {
        this(player, configWrapper.isSet("Inventory.size") ? InventoryType.getChestInventoryWithSize(configWrapper.getOrSetDefault("Inventory.size", 36)) : InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "GENERIC_9X5")), configWrapper, locale, true);
    }

    protected MultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper configWrapper, final boolean update) {
        this(player, type, configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update);
    }

    protected MultiPageInventoryWrapper(final P player, final ConfigWrapper configWrapper, final boolean update) {
        this(player, configWrapper.isSet("Inventory.size") ? InventoryType.getChestInventoryWithSize(configWrapper.getOrSetDefault("Inventory.size", 36)) : InventoryType.valueOf(configWrapper.getOrSetDefault("Inventory.type", "GENERIC_9X5")), configWrapper, Providers.get(LocaleProvider.class).getFallbackLocale(), update);
    }

    protected MultiPageInventoryWrapper(final P player, final ConfigWrapper config, final Locale locale, final boolean update) {
        this(player, config.isSet("Inventory.size") ? InventoryType.getChestInventoryWithSize(config.getOrSetDefault("Inventory.size", 36)) : InventoryType.valueOf(config.getOrSetDefault("Inventory.type", "GENERIC_9X5")), config, locale, update);
    }

    protected MultiPageInventoryWrapper(final P player, final InventoryType type, final ConfigWrapper config, final Locale locale, final boolean update) {
        super(player, type, locale);
        this.config = config;
        setDefaultReplacer(() -> new String[0]);
        childs.add(new ChildInventoryWrapper<>(player, type, locale, config, this));
        setTitle(config.getLocalizedString(locale, "Inventory", ".title", "&6Inventory"));
        registerActionHandler("noAction", click -> CallResult.DENY_GRABBING);

        registerActionHandler("nextPage", click -> {
            if(currentPage == childs.size())
                return CallResult.DENY_GRABBING;
            currentPage ++;
            updateInventory();
            build();
            return CallResult.DENY_GRABBING;
        });

        registerActionHandler("previousPage", click -> {
            if(currentPage == 1)
                return CallResult.DENY_GRABBING;
            currentPage --;
            updateInventory();
            build();
            return CallResult.DENY_GRABBING;
        });

        itemsPerPage = config.getOrSetDefault("Inventory.itemsPerPage", 45);
        nextPageItemSlots = config.getOrSetDefault("Inventory.nextPageSlots", Collections.singletonList(53));
        previousPageItemSlots = config.getOrSetDefault("Inventory.previousPageSlots", Collections.singletonList(45));
        nextPageItem = config.getItemStack("Inventory.nextPageItem", locale, replacer);
        previousPageItem = config.getItemStack("Inventory.previousPageItem", locale, replacer);

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
        if(currentPage > 1) {
            previousPageItemSlots.forEach(i -> set(i, previousPageItem, "previousPage"));
        }
        if(childs.size() > currentPage) {
            nextPageItemSlots.forEach(i -> set(i, nextPageItem, "nextPage"));
        }
    }

    @Override
    public void setDefaultReplacer(final Supplier<String[]> replacer) {
        super.setDefaultReplacer(() -> {
            final List<String> out = new ArrayList<>(Arrays.asList(replacer.get()));
            out.addAll(Arrays.asList("%page%", Integer.toString(currentPage), "%pages%", Integer.toString(childs.size())));
            return out.toArray(new String[0]);
        });
    }

    public void newPage() {
        childs.add(new ChildInventoryWrapper<>(getPlayer(), getInventoryType(), getLocale(), config, this));
        updateInventory();
        currentPage ++;
        updateInventory();
    }

    @Override
    public void setInventoryType(final InventoryType type) {
        super.setInventoryType(type);
        childs.forEach(i -> i.setInventoryType(type));
    }

    @Override
    public int getNextFreeSlot() {
        int itemCount = 0;
        for (int i = 0; i < getSize(); i++) {
            if(nextPageItemSlots.contains(i) || previousPageItemSlots.contains(i))
                continue;
            final InventoryItem item = get(i);
            if(item == null) {
                return i;
            } else {
                itemCount ++;
                if(itemCount >= itemsPerPage) {
                    return -1;
                }
            }
        }
        return -1;
    }

    @Override
    public void clear() {
        childs.clear();
        childs.add(new ChildInventoryWrapper<>(getPlayer(), getInventoryType(), getLocale(), config, this));
        setCurrentPage(1);
        updateInventory();
    }

    @Override
    public void onExit(boolean inventorySwitch) {
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }

    public List<AbstractBaseInventoryWrapper<P, I, INV>> getPages() {
        return new ArrayList<>(childs);
    }

    public void setItemsPerPage(final int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getCurrentPageIndex() {
        return currentPage;
    }

    public AbstractBaseInventoryWrapper<P, I, INV> getCurrentPage() {
        return childs.get(currentPage-1);
    }

    @Override
    public void set(final int slot, final I stack, final String actionHandler, final Arguments args) {
        getCurrentPage().set(slot, stack, actionHandler, args);
    }

    public void setPlaceHolder(final ItemStackWrapper placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void setCustomTitle(final boolean customTitle) {
        this.customTitle = customTitle;
    }

    @Override
    public void add(final I stack, final String actionHandler, final Arguments args) {
        final int slot = getNextFreeSlot();
        if(slot == -1) {
            if(childs.size() > currentPage) {
                currentPage = childs.size();
                add(stack, actionHandler, args);
                return;
            }
            newPage();
            if(getNextFreeSlot() == -1) {
                throw new RuntimeException("[MCCommons] Inventory has no space for additional items. Inventory size is "+getSize()+" and type is "+getInventoryType().name()+". There are "+getInventoryItemMap().size()+" items preconfigured in a new page. Max items per page are "+itemsPerPage+".");
            }
            add(stack, actionHandler, args);
        } else {
            set(slot, stack, actionHandler, args);
        }
    }

    @Override
    public Map<Integer, InventoryItem> getInventoryItemMap() {
        return getCurrentPage().getInventoryItemMap();
    }

    @Override
    public INV build() {
        return getCurrentPage().build();
    }

    public INV buildFirstPage() {
        setCurrentPage(1);
        updateInventory();
        return getCurrentPage().build();
    }

    @Override
    public InventoryItem get(int slot) {
        return getCurrentPage().get(slot);
    }

    @Override
    public String getTitle() {
        return getCurrentPage().getTitle();
    }

    @Override
    public void setTitle(final String title) {
        customTitle = true;
        super.setTitle(title);
        childs.forEach(it -> it.setTitle(title));
    }

}
