package de.exceptionflug.mccommons.plugin.proxy;

import de.exceptionflug.mccommons.config.proxy.ProxyConfig;
import de.exceptionflug.mccommons.config.proxy.ProxyConfigProxyYamlConfigWrapper;
import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.AsyncProvider;
import de.exceptionflug.mccommons.core.providers.WorkingDirectoryProvider;
import de.exceptionflug.mccommons.inventories.api.InventoryBuilder;
import de.exceptionflug.mccommons.inventories.api.PlayerWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;
import de.exceptionflug.mccommons.inventories.proxy.builder.ProtocolizeInventoryBuilder;
import de.exceptionflug.mccommons.inventories.proxy.converters.ItemTypeConverter;
import de.exceptionflug.mccommons.inventories.proxy.converters.ProtocolizeClickTypeConverter;
import de.exceptionflug.mccommons.inventories.proxy.converters.ProtocolizeInventoryTypeConverter;
import de.exceptionflug.mccommons.inventories.proxy.converters.ProtocolizeItemTypeConverter;
import de.exceptionflug.mccommons.inventories.proxy.item.ProtocolizeItemStackWrapper;
import de.exceptionflug.mccommons.inventories.proxy.listener.InventoryListener;
import de.exceptionflug.mccommons.plugin.proxy.commands.ConfigReloadCommand;
import de.exceptionflug.mccommons.plugin.proxy.converter.ItemStackConverter;
import de.exceptionflug.mccommons.plugin.proxy.converter.PlayerConverter;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.inventory.InventoryType;
import dev.simplix.protocolize.items.ItemStack;
import dev.simplix.protocolize.items.ItemType;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class MCCommonsProxyBootstrap {
	private final Plugin plugin;

	public MCCommonsProxyBootstrap(final Plugin plugin) {
		this.plugin = plugin;
	}

	public void enableMCCommons() {
		Providers.register(Plugin.class, plugin);
		Providers.register(InventoryBuilder.class, new ProtocolizeInventoryBuilder());
		Providers.register(AsyncProvider.class, new AsyncProvider() {
			@Override
			public void async(Runnable runnable) {
				ProxyServer.getInstance().getScheduler().runAsync(plugin, runnable);
			}
		});
		Providers.register(WorkingDirectoryProvider.class, new WorkingDirectoryProvider() {
			@Override
			public File getWorkingDirectory() {
				return plugin.getDataFolder();
			}
		});

		ConfigFactory.register(ConfigWrapper.class, ProxyConfigProxyYamlConfigWrapper::new);
		ConfigFactory.register(ProxyConfig.class, ProxyConfigProxyYamlConfigWrapper::new);
		Converters.register(ConfigItemStack.class, ItemStack.class, new ItemStackConverter());
		Converters.register(UserConnection.class, PlayerWrapper.class, new PlayerConverter());
		Converters.register(de.exceptionflug.mccommons.inventories.api.InventoryType.class, InventoryType.class, new ProtocolizeInventoryTypeConverter());
		Converters.register(ItemType.class, de.exceptionflug.mccommons.inventories.api.item.ItemType.class, new ProtocolizeItemTypeConverter());
		Converters.register(de.exceptionflug.mccommons.inventories.api.item.ItemType.class, ItemType.class, new ItemTypeConverter());
		Converters.register(ItemStack.class, ItemStackWrapper.class, src -> new ProtocolizeItemStackWrapper((ItemStack) src));
		Converters.register(ItemStackWrapper.class, ItemStack.class, src -> ((ItemStackWrapper) src).getHandle());
		Converters.register(ProtocolizeItemStackWrapper.class, ItemStack.class, src -> ((ItemStackWrapper) src).getHandle());
		Converters.register(ClickType.class, de.exceptionflug.mccommons.inventories.api.ClickType.class, new ProtocolizeClickTypeConverter());

		ProxyServer.getInstance().getPluginManager().registerListener(plugin, new InventoryListener());
		ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new ConfigReloadCommand());
	}
}
