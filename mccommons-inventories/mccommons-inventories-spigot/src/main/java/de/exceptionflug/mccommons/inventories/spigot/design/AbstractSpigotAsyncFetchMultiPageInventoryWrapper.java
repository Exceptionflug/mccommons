package de.exceptionflug.mccommons.inventories.spigot.design;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.inventories.api.InventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryType;
import de.exceptionflug.mccommons.inventories.api.design.AbstractAsyncFetchMultiPageInventoryWrapper;
import de.exceptionflug.mccommons.inventories.spigot.utils.Schedulable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.logging.Level;

public abstract class AbstractSpigotAsyncFetchMultiPageInventoryWrapper<T> extends AbstractAsyncFetchMultiPageInventoryWrapper<Player, ItemStack, Inventory, T> implements Schedulable {

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, InventoryType type, ConfigWrapper configWrapper) {
        super(player, type, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
    }

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, ConfigWrapper configWrapper) {
        super(player, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
    }

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, InventoryType type, ConfigWrapper configWrapper, Locale locale) {
        super(player, type, configWrapper, locale);
    }

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, ConfigWrapper configWrapper, Locale locale) {
        super(player, configWrapper, locale);
    }

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, InventoryType type, ConfigWrapper configWrapper, boolean update, boolean loading) {
        super(player, type, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update, loading);
    }

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, ConfigWrapper configWrapper, boolean update, boolean loading) {
        super(player, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update, loading);
    }

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, ConfigWrapper config, Locale locale, boolean update, boolean loading) {
        super(player, config, locale, update, loading);
    }

    protected AbstractSpigotAsyncFetchMultiPageInventoryWrapper(Player player, InventoryType type, ConfigWrapper config, Locale locale, boolean update, boolean loading) {
        super(player, type, config, locale, update, loading);
    }

    @Override
    protected void runLater(Runnable runnable, int delay) {
        later(runnable, delay);
    }

    @Override
    public void async0(final Runnable runnable) {
        async(runnable);
    }

    @Override
    public void sync0(final Runnable runnable) {
        sync(runnable);
    }

    @Override
    public void onException(final Exception exception, final InventoryItem inventoryItem) {
        if(inventoryItem != null) {
            getPlayer().sendMessage("§cAn internal exception occurred while handling action §6"+inventoryItem.getActionHandler()+" §cof §6"+getClass().getSimpleName()+"§c at page §6"+getCurrentPageIndex());
            Bukkit.getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred while handling action "+inventoryItem.getActionHandler()+" of "+getClass().getName(), exception);
        } else {
            getPlayer().sendMessage("§cAn internal exception occurred §cat §6"+getClass().getSimpleName()+"§c at page §6"+getCurrentPageIndex());
            Bukkit.getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred at "+getClass().getName(), exception);
        }
    }

}
