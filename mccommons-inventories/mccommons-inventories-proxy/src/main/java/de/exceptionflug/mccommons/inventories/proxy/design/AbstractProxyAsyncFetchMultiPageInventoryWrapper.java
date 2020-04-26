package de.exceptionflug.mccommons.inventories.proxy.design;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.inventories.api.DataInventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryType;
import de.exceptionflug.mccommons.inventories.api.design.AbstractAsyncFetchMultiPageInventoryWrapper;
import de.exceptionflug.mccommons.inventories.proxy.utils.Schedulable;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class AbstractProxyAsyncFetchMultiPageInventoryWrapper<T> extends AbstractAsyncFetchMultiPageInventoryWrapper<ProxiedPlayer, ItemStack, Inventory, T> implements Schedulable {

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper configWrapper) {
        super(player, type, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
    }

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, ConfigWrapper configWrapper) {
        super(player, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
    }

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper configWrapper, Locale locale) {
        super(player, type, configWrapper, locale);
    }

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, ConfigWrapper configWrapper, Locale locale) {
        super(player, configWrapper, locale);
    }

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper configWrapper, boolean update, boolean loading) {
        super(player, type, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update, loading);
    }

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, ConfigWrapper configWrapper, boolean update, boolean loading) {
        super(player, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update, loading);
    }

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, ConfigWrapper config, Locale locale, boolean update, boolean loading) {
        super(player, config, locale, update, loading);
    }

    protected AbstractProxyAsyncFetchMultiPageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper config, Locale locale, boolean update, boolean loading) {
        super(player, type, config, locale, update, loading);
    }

    @Override
    public void async0(final Runnable runnable) {
        async(runnable);
    }

    @Override
    public void sync0(final Runnable runnable) {
        runnable.run();
    }

    @Override
    public void onException(final Exception exception, final InventoryItem inventoryItem) {
        if(inventoryItem != null) {
            getPlayer().sendMessage("§cAn internal exception occurred while handling action §6"+inventoryItem.getActionHandler()+" §cof §6"+getClass().getSimpleName()+" §cat page §6"+getCurrentPageIndex());
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred while handling action "+inventoryItem.getActionHandler()+" of "+getClass().getName()+" at page "+getCurrentPageIndex(), exception);
        } else {
            getPlayer().sendMessage("§cAn internal exception occurred §cat §6"+getClass().getSimpleName()+" §cat page §6"+getCurrentPageIndex());
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred at "+getClass().getName(), exception);
        }
    }

    @Override
    protected void runLater(Runnable runnable, int ticks) {
        later(runnable, ticks / 20, TimeUnit.SECONDS);
    }

}
