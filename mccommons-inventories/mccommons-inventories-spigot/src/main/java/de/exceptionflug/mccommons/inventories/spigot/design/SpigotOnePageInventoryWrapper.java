package de.exceptionflug.mccommons.inventories.spigot.design;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.inventories.api.InventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryType;
import de.exceptionflug.mccommons.inventories.api.design.OnePageInventoryWrapper;
import de.exceptionflug.mccommons.inventories.spigot.utils.Schedulable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.logging.Level;

public class SpigotOnePageInventoryWrapper extends OnePageInventoryWrapper<Player, ItemStack, Inventory> implements Schedulable {

    protected SpigotOnePageInventoryWrapper(Player player, ConfigWrapper configWrapper) {
        super(player, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
    }

    protected SpigotOnePageInventoryWrapper(Player player, InventoryType type, ConfigWrapper configWrapper) {
        super(player, type, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()));
    }

    protected SpigotOnePageInventoryWrapper(Player player, ConfigWrapper configWrapper, Locale locale) {
        super(player, configWrapper, locale);
    }

    protected SpigotOnePageInventoryWrapper(Player player, InventoryType type, ConfigWrapper configWrapper, Locale locale) {
        super(player, type, configWrapper, locale);
    }

    protected SpigotOnePageInventoryWrapper(Player player, InventoryType type, ConfigWrapper configWrapper, boolean update) {
        super(player, type, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update);
    }

    protected SpigotOnePageInventoryWrapper(Player player, ConfigWrapper configWrapper, boolean update) {
        super(player, configWrapper, Providers.get(LocaleProvider.class).provide(player.getUniqueId()), update);
    }

    protected SpigotOnePageInventoryWrapper(Player player, InventoryType type, ConfigWrapper configWrapper, Locale locale, boolean update) {
        super(player, type, configWrapper, locale, update);
    }

    public SpigotOnePageInventoryWrapper(Player player, ConfigWrapper configWrapper, Locale locale, boolean update) {
        super(player, configWrapper, locale, update);
    }

    @Override
    public void onException(final Exception exception, final InventoryItem inventoryItem) {
        if(inventoryItem != null) {
            getPlayer().sendMessage("§cAn internal exception occurred while handling action §6"+inventoryItem.getActionHandler()+" §cof §6"+getClass().getSimpleName());
            Bukkit.getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred while handling action "+inventoryItem.getActionHandler()+" of "+getClass().getName(), exception);
        } else {
            getPlayer().sendMessage("§cAn internal exception occurred §cat §6"+getClass().getSimpleName());
            Bukkit.getLogger().log(Level.SEVERE, "[MCCommons] An internal exception occurred at "+getClass().getName(), exception);
        }
    }

}
