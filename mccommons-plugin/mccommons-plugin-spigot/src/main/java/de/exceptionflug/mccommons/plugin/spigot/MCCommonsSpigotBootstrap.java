package de.exceptionflug.mccommons.plugin.spigot;

import com.flowpowered.nbt.CompoundTag;
import de.exceptionflug.mccommons.config.remote.client.RemoteConfigClient;
import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.config.shared.RemoteClientProvider;
import de.exceptionflug.mccommons.config.spigot.SpigotConfig;
import de.exceptionflug.mccommons.config.spigot.SpigotConfigSpigotYamlConfigWrapper;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.AsyncProvider;
import de.exceptionflug.mccommons.core.providers.WorkingDirectoryProvider;
import de.exceptionflug.mccommons.holograms.Holograms;
import de.exceptionflug.mccommons.inventories.api.InventoryBuilder;
import de.exceptionflug.mccommons.inventories.api.InventoryType;
import de.exceptionflug.mccommons.inventories.api.PlayerWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemStackWrapper;
import de.exceptionflug.mccommons.inventories.spigot.builder.SpigotInventoryBuilder;
import de.exceptionflug.mccommons.inventories.spigot.converters.*;
import de.exceptionflug.mccommons.inventories.spigot.item.SpigotItemStackWrapper;
import de.exceptionflug.mccommons.inventories.spigot.listener.InventoryListener;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;
import de.exceptionflug.mccommons.inventories.spigot.utils.ServerVersionProvider;
import de.exceptionflug.mccommons.plugin.spigot.commands.ConfigReloadCommand;
import de.exceptionflug.mccommons.plugin.spigot.commands.HologramReloadCommand;
import de.exceptionflug.mccommons.plugin.spigot.converter.ItemStackConverter;
import de.exceptionflug.mccommons.plugin.spigot.converter.PlayerConverter;
import de.exceptionflug.protocolize.items.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MCCommonsSpigotBootstrap {
    public void enable(final JavaPlugin plugin) {
        try {
            Providers.register(InventoryBuilder.class, new SpigotInventoryBuilder());
            Providers.register(JavaPlugin.class, plugin);
            Providers.register(RemoteClientProvider.class, new RemoteClientProvider() {
                @Override
                public RemoteConfigClient get() {
                    final ConfigWrapper configWrapper = ConfigFactory.create("MCCommons", SpigotMCCommonsPlugin.class, SpigotConfig.class);
                    return new RemoteConfigClient(configWrapper.getOrSetDefault("RemoteServer.url", "http://localhost:8881"), configWrapper.getOrSetDefault("RemoteServer.url", "x7834HgsTSds9"));
                }
            });

            Providers.register(AsyncProvider.class, new AsyncProvider() {
                @Override
                public void async(final Runnable runnable) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
                }
            });

            Providers.register(WorkingDirectoryProvider.class, new WorkingDirectoryProvider() {
                @Override
                public File getWorkingDirectory() {
                    return plugin.getDataFolder();
                }
            });

            Providers.register(ServerVersionProvider.class, new ServerVersionProvider());
            ConfigFactory.register(SpigotConfig.class, SpigotConfigSpigotYamlConfigWrapper::new);
            ConfigFactory.register(ConfigWrapper.class, SpigotConfigSpigotYamlConfigWrapper::new);
            ConfigFactory.registerRemote(ConfigWrapper.class, s -> new SpigotConfigSpigotYamlConfigWrapper(s, configData -> ConfigFactory.getRemoteConfigClient().getConfigService().update(configData).blockingSubscribe(), () -> ConfigFactory.getRemoteConfigClient().getConfigService().getConfig(s.getRemotePath()).blockingFirst()));
            ConfigFactory.registerRemote(SpigotConfig.class, s -> new SpigotConfigSpigotYamlConfigWrapper(s, configData -> ConfigFactory.getRemoteConfigClient().getConfigService().update(configData).blockingSubscribe(), () -> ConfigFactory.getRemoteConfigClient().getConfigService().getConfig(s.getRemotePath()).blockingFirst()));

            try {
                Converters.register(ReflectionUtil.getClass("{obc}.entity.CraftPlayer"), PlayerWrapper.class, new PlayerConverter());
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            }

            Converters.register(ConfigItemStack.class, ItemStack.class, new ItemStackConverter());
            Converters.register(de.exceptionflug.protocolize.items.ItemStack.class, ItemStack.class, new ProtocolizeItemStackConverter());
            Converters.register(ItemStack.class, de.exceptionflug.protocolize.items.ItemStack.class, new BukkitItemStackConverter());
            Converters.register(ItemType.class, MaterialData.class, new ItemTypeMaterialDataConverter());
            Converters.register(MaterialData.class, ItemType.class, new MaterialDataItemTypeConverter());
            Converters.register(CompoundTag.class, ReflectionUtil.getClass("{nms}.NBTTagCompound"), new FlowNbtNmsNbtConverter());
            Converters.register(ReflectionUtil.getClass("{nms}.NBTTagCompound"), CompoundTag.class, new NmsNbtFlowNbtConverter());
            Converters.register(ItemStack.class, ItemStackWrapper.class, src -> new SpigotItemStackWrapper((ItemStack) src));
            Converters.register(ItemStackWrapper.class, ItemStack.class, src -> ((ItemStackWrapper) src).getHandle());
            Converters.register(SpigotItemStackWrapper.class, ItemStack.class, src -> ((ItemStackWrapper) src).getHandle());
            Converters.register(ClickType.class, de.exceptionflug.mccommons.inventories.api.ClickType.class, new SpigotClickTypeConverter());
            Converters.register(InventoryType.class, org.bukkit.event.inventory.InventoryType.class, new SpigotInventoryTypeConverter());
            Converters.register(ReflectionUtil.getClass("{obc}.inventory.CraftItemStack"), ItemStack.class, new CraftItemStackConverter());

            Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
            plugin.getCommand("mcrl").setExecutor(new ConfigReloadCommand());
            plugin.getCommand("hrl").setExecutor(new HologramReloadCommand());

            Holograms.init(plugin);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }
}
