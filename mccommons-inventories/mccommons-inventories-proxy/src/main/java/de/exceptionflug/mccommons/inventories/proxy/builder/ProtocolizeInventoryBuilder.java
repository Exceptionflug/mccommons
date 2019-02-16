package de.exceptionflug.mccommons.inventories.proxy.builder;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.InventoryBuilder;
import de.exceptionflug.mccommons.inventories.api.InventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryWrapper;
import de.exceptionflug.mccommons.inventories.proxy.utils.ItemUtils;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.*;

public class ProtocolizeInventoryBuilder implements InventoryBuilder {

    private final Map<UUID, Map.Entry<InventoryWrapper, Long>> buildMap = new LinkedHashMap<>();
    private final List<InventoryWrapper> wrappers = new LinkedList<>();

    @Override
    public <T> T build(T prebuild, InventoryWrapper wrapper) {
        boolean reopen = false;
        boolean register = prebuild == null;
        if(prebuild instanceof Inventory) {
            final Inventory inventory = (Inventory) prebuild;
            if(!ComponentSerializer.toString(inventory.getTitle()).equals(ComponentSerializer.toString(new TextComponent(wrapper.getTitle()))) || inventory.getSize() != wrapper.getSize() || inventory.getType() != Converters.convert(wrapper.getInventoryType(), InventoryType.class)) {
                prebuild = (T) makeInv(wrapper);
                reopen = true;
            }
        } else {
            prebuild = (T) makeInv(wrapper);
        }
        final Inventory inventory = (Inventory) prebuild;
        for (int i = 0; i < wrapper.getSize(); i++) {
            final InventoryItem item = (InventoryItem) wrapper.getInventoryItemMap().get(i);
            final ItemStack currentStack = inventory.getItem(i);
            if(item == null) {
                if(currentStack != null) {
                    inventory.setItem(i, ItemStack.NO_DATA);
                }
            }
            if(item != null) {
                if(currentStack == null) {
                    if(item.getItemStackWrapper() == null) {
                        ProxyServer.getInstance().getLogger().severe("InventoryItem's ItemStackWrapper is null @ slot "+i);
                        continue;
                    }
                    inventory.setItem(i, item.getItemStackWrapper().getHandle());
                } else {
                    if(item.getItemStackWrapper().getType() == de.exceptionflug.mccommons.inventories.api.item.ItemType.PLAYER_HEAD && currentStack.getType() == ItemType.PLAYER_HEAD) {
                        final String ownaz1 = ItemUtils.getSkullOwner(item.getItemStackWrapper().getHandle());
                        final String ownaz2 = ItemUtils.getSkullOwner(currentStack);
                        if(ownaz1 != null && ownaz1.equals(ownaz2)) {
                            continue;
                        }
                    }
                    if(!currentStack.equals(item.getItemStackWrapper().getHandle())) {
                        inventory.setItem(i, item.getItemStackWrapper().getHandle());
                    }
                }
            }
        }
        buildMap.put(((ProxiedPlayer)wrapper.getPlayer()).getUniqueId(), new AbstractMap.SimpleEntry<>(wrapper, System.currentTimeMillis()));
        if(register)
            wrappers.add(wrapper);
        if(reopen)
            InventoryModule.sendInventory((ProxiedPlayer) wrapper.getPlayer(), inventory);
        return prebuild;
    }

    private Inventory makeInv(final InventoryWrapper wrapper) {
        if(wrapper.getInventoryType() == de.exceptionflug.mccommons.inventories.api.InventoryType.CHEST) {
            return new Inventory(wrapper.getSize(), new TextComponent(wrapper.getTitle()));
        } else {
            final InventoryType type = Converters.convert(wrapper.getInventoryType(), InventoryType.class);
            return new Inventory(type, type.getTypicalSize(), new TextComponent(wrapper.getTitle()));
        }
    }

    @Override
    public InventoryWrapper getWrapperByHandle(final Object handle) {
        for(final InventoryWrapper wrapper : wrappers) {
            if(wrapper.equals(handle))
                return wrapper;
        }
        return null;
    }

    @Override
    public void destroyWrappersBy(final UUID uniqueId) {
        wrappers.removeIf(wrapper -> ((ProxiedPlayer)wrapper.getPlayer()).getUniqueId().equals(uniqueId));
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
        InventoryModule.sendInventory((ProxiedPlayer) wrapper.getPlayer(), (Inventory) wrapper.build());
    }



}
