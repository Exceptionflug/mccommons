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
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
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
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SpigotMCCommonsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new MCCommonsSpigotBootstrap().enable(this);
    }
}
