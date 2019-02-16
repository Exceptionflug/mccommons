package de.exceptionflug.mccommons.inventories.spigot.listener;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        final Inventory inventory = e.getClickedInventory();
        if (inventory == null) {
            return;
        }
        final InventoryWrapper wrapper = Providers.get(InventoryBuilder.class).getWrapperByHandle(inventory);
        if (wrapper == null)
            return;
        final InventoryItem item = wrapper.get(e.getSlot());
        if (item == null) {
            if(wrapper.getCustomActionHandler() != null) {
                try {
                    final CallResult callResult = wrapper.getCustomActionHandler().handle(new Click(Converters.convert(e.getClick(), ClickType.class), wrapper, null, e.getSlot()));
                    e.setCancelled(callResult == null || callResult == CallResult.DENY_GRABBING);
                } catch (final Exception ex) {
                    e.setCancelled(true);
                    wrapper.onException(ex, null);
                }
            }
            return;
        }
        final ActionHandler actionHandler = wrapper.getActionHandler(item.getActionHandler());
        if (actionHandler == null)
            return;
        try {
            final CallResult callResult = actionHandler.handle(new Click(Converters.convert(e.getClick(), ClickType.class), wrapper, item.getItemStackWrapper(), e.getSlot()));
            e.setCancelled(callResult == null || callResult == CallResult.DENY_GRABBING);
        } catch (final Exception ex) {
            e.setCancelled(true);
            wrapper.onException(ex, item);
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        Providers.get(InventoryBuilder.class).destroyWrappersBy(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent e) {
        final Inventory inventory = e.getInventory();
        final InventoryBuilder inventoryBuilder = Providers.get(InventoryBuilder.class);
        if (inventory == null) {
            return;
        }
        final InventoryWrapper wrapper = inventoryBuilder.getWrapperByHandle(inventory);
        if (wrapper == null)
            return;
        final Map.Entry<InventoryWrapper, Long> lastBuild = inventoryBuilder.getLastBuild(e.getPlayer().getUniqueId());
        if (lastBuild.getKey().getInternalId() == wrapper.getInternalId() && (System.currentTimeMillis() - lastBuild.getValue()) <= 55)
            return;
        wrapper.onExit((System.currentTimeMillis() - lastBuild.getValue()) <= 55);
        inventoryBuilder.uncache(wrapper);
    }

}
