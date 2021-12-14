package de.exceptionflug.mccommons.inventories.proxy.listener;

import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.InventoryBuilder;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class InventoryListener implements Listener {

	@EventHandler
	public void onQuit(final PlayerDisconnectEvent e) {
		Providers.get(InventoryBuilder.class).destroyWrappersBy(e.getPlayer().getUniqueId());
	}
}
