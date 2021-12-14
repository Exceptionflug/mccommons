package de.exceptionflug.mccommons.inventories.proxy.design;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.inventories.api.InventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryType;
import de.exceptionflug.mccommons.inventories.api.design.OnePageInventoryWrapper;
import de.exceptionflug.mccommons.inventories.proxy.utils.Schedulable;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.logging.Level;

public class ProxyOnePageInventoryWrapper extends OnePageInventoryWrapper<ProxiedPlayer, ItemStack, Inventory> implements Schedulable {

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, ConfigWrapper configWrapper) {
		super(player, configWrapper, player == null ? Locale.GERMAN : Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
	}

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper configWrapper) {
		super(player, type, configWrapper, player == null ? Locale.GERMAN : Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
	}

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, ConfigWrapper configWrapper, Locale locale) {
		super(player, configWrapper, locale);
	}

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper configWrapper, Locale locale) {
		super(player, type, configWrapper, locale);
	}

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper configWrapper, boolean update) {
		super(player, type, configWrapper, player == null ? Locale.GERMAN : Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update);
	}

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, ConfigWrapper configWrapper, boolean update) {
		super(player, configWrapper, player == null ? Locale.GERMAN : Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update);
	}

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, ConfigWrapper configWrapper, Locale locale, boolean update) {
		super(player, configWrapper, locale, update);
	}

	protected ProxyOnePageInventoryWrapper(ProxiedPlayer player, InventoryType type, ConfigWrapper configWrapper, Locale locale, boolean update) {
		super(player, type, configWrapper, locale, update);
	}

	@Override
	public void onException(final Exception exception, final InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			getPlayer().sendMessage("§cAn internal exception occurred while handling action §6" + inventoryItem.getActionHandler() + " §cof §6" + getClass().getSimpleName());
			ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred while handling action " + inventoryItem.getActionHandler() + " of " + getClass().getName(), exception);
		} else {
			getPlayer().sendMessage("§cAn internal exception occurred §cat §6" + getClass().getSimpleName());
			ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred at " + getClass().getName(), exception);
		}
	}

}
