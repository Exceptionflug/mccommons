package de.exceptionflug.mccommons.inventories.proxy.builder;

import com.google.common.collect.Sets;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.*;
import de.exceptionflug.mccommons.inventories.proxy.utils.ItemUtils;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.*;

public class ProtocolizeInventoryBuilder implements InventoryBuilder {

	private final Map<UUID, Map.Entry<InventoryWrapper, Long>> buildMap = new LinkedHashMap<>();
	private final Set<InventoryWrapper> wrappers = Sets.newConcurrentHashSet();

	@Override
	public <T> T build(T prebuild, final InventoryWrapper wrapper) {
//        boolean reopen = true;
		final boolean register = prebuild == null;
		if (prebuild instanceof Inventory) {
			final Inventory inventory = (Inventory) prebuild;
			if (!ComponentSerializer.toString((BaseComponent) inventory.title())
				.equals(ComponentSerializer.toString(new TextComponent(wrapper.getTitle()))) ||
				inventory.type().getTypicalSize(
					Converters.convert(wrapper.getPlayer(), PlayerWrapper.class)
						.getProtocolVersion()) != wrapper.getSize()
				|| inventory.type() != Converters
				.convert(wrapper.getInventoryType(), InventoryType.class)) {
				prebuild = (T) makeInv(wrapper);
//                reopen = true;
			}
		} else {
			prebuild = (T) makeInv(wrapper);
		}
		final Inventory inventory = (Inventory) prebuild;
		for (int i = 0; i < wrapper.getSize(); i++) {
			final InventoryItem item = (InventoryItem) wrapper.getInventoryItemMap().get(i);
			final ItemStack currentStack = inventory.item(i);
			if (item == null) {
				if (currentStack != null) {
					inventory.item(i, ItemStack.NO_DATA);
				}
			}
			if (item != null) {
				if (currentStack == null) {
					if (item.getItemStackWrapper() == null) {
						ProxyServer.getInstance().getLogger()
							.severe("InventoryItem's ItemStackWrapper is null @ slot " + i);
						continue;
					}
					inventory.item(i, item.getItemStackWrapper().getHandle());
				} else {
					if (item.getItemStackWrapper().getType()
						== de.exceptionflug.mccommons.inventories.api.item.ItemType.PLAYER_HEAD
						&& currentStack.itemType() == ItemType.PLAYER_HEAD) {
						final String ownaz1 = ItemUtils
							.getSkullOwner(item.getItemStackWrapper().getHandle());
						final String ownaz2 = ItemUtils.getSkullOwner(currentStack);
						if (ownaz1 != null && ownaz1.equals(ownaz2)) {
							continue;
						}
					}
					if (!currentStack.equals(item.getItemStackWrapper().getHandle())) {
						inventory.item(i, item.getItemStackWrapper().getHandle());
					}
				}
			}
		}
		buildMap.put(((ProxiedPlayer) wrapper.getPlayer()).getUniqueId(),
			new AbstractMap.SimpleEntry<>(wrapper, System.currentTimeMillis()));
		if (register) {
			wrappers.add(wrapper);
		}
//        if(reopen)
		Protocolize.playerProvider()
				.player(((ProxiedPlayer) wrapper.getPlayer()).getUniqueId())
				.openInventory(inventory);
		return prebuild;
	}

	private Inventory makeInv(final InventoryWrapper wrapper) {
		final InventoryType type = Converters
			.convert(wrapper.getInventoryType(), InventoryType.class);
		Inventory inventory = new Inventory(type);
		inventory.title(new TextComponent(wrapper.getTitle()));

		inventory.onClick(inventoryClick -> onClick(inventoryClick, wrapper));
		inventory.onClose(inventoryClose -> onClose(inventoryClose, wrapper));

		return inventory;
	}

	private void onClick(InventoryClick inventoryClick, InventoryWrapper wrapper) {
		if (inventoryClick.clickType() == null) {
			return;
		}
		if (inventoryClick.player() == null) {
			return;
		}
		if (inventoryClick.clickedItem() == null) {
			return;
		}
		Inventory i = inventoryClick.inventory();
		if (i == null) {
			return;
		}
//            ProxyServer.getInstance().broadcast("Clicked inventory");
//            ProxyServer.getInstance().broadcast("Clicked menu: " + menu.getClass().getSimpleName() + " @ slot " + inventoryClick.slot());

		InventoryItem item = wrapper.get(inventoryClick.slot());
		ClickType type = inventoryClick.clickType();
		if (item == null) {
//                ProxyServer.getInstance().broadcast("Clicked nothing");
			if (wrapper.getCustomActionHandler() != null) {
				try {
					CallResult callResult = wrapper
							.getCustomActionHandler()
							.handle(new Click(Converters.convert(type, de.exceptionflug.mccommons.inventories.api.ClickType.class), wrapper, null, inventoryClick.slot()));
					inventoryClick.cancelled(callResult == null || callResult == CallResult.DENY_GRABBING);
				} catch (Exception ex) {
					inventoryClick.cancelled(true);
					wrapper.onException(ex, null);
				}
			}
			return;
		}
//            ProxyServer.getInstance().broadcast("Clicked " + item.displayName());
		ActionHandler actionHandler = wrapper.getActionHandler(item.getActionHandler());
		if (actionHandler == null) {
			inventoryClick.cancelled(true);
			return;
		}
		try {
			final CallResult callResult = actionHandler.handle(new Click(
					Converters.convert(type, de.exceptionflug.mccommons.inventories.api.ClickType.class),
					wrapper,
					item,
					inventoryClick.slot()));
			inventoryClick.cancelled(callResult == null || callResult == CallResult.DENY_GRABBING);
		} catch (final Exception ex) {
			inventoryClick.cancelled(true);
			wrapper.onException(ex, item);
		}
	}

	private void onClose(InventoryClose inventoryClose, InventoryWrapper wrapper) {
		final Inventory inventory = inventoryClose.inventory();
		if (inventory == null)
			return;
		if (wrapper == null)
			return;
		final Map.Entry<InventoryWrapper, Long> lastBuild = getLastBuild(inventoryClose.player().uniqueId());
		if (lastBuild.getKey().getInternalId() == wrapper.getInternalId() && (System.currentTimeMillis() - lastBuild.getValue()) <= 55)
			return;
		wrapper.onExit((System.currentTimeMillis() - lastBuild.getValue()) <= 55);
		uncache(wrapper);
	}

	@Override
	public InventoryWrapper getWrapperByHandle(final Object handle) {
		if (handle == null) {
			return null;
		}
		for (final InventoryWrapper wrapper : wrappers) {
			if (wrapper.equals(handle)) {
				return wrapper;
			}
		}
		return null;
	}

	@Override
	public void destroyWrappersBy(final UUID uniqueId) {
		wrappers.removeIf(
			wrapper -> ((ProxiedPlayer) wrapper.getPlayer()).getUniqueId().equals(uniqueId));
		buildMap.remove(uniqueId);
	}

	@Override
	public Map.Entry<InventoryWrapper, Long> getLastBuild(final UUID uniqueId) {
		return buildMap.get(uniqueId);
	}

	@Override
	public void uncache(final InventoryWrapper wrapper) {
		wrappers.remove(wrapper);
	}

	@Override
	public void open(final InventoryWrapper wrapper) {
		Protocolize.playerProvider()
				.player(((ProxiedPlayer)wrapper.getPlayer()).getUniqueId())
				.openInventory((Inventory) wrapper.build());
	}

	@Override
	public void addWrapper(InventoryWrapper wrapper) {
		wrappers.add(wrapper);
	}
}
