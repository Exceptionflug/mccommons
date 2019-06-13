package de.exceptionflug.mccommons.inventories.spigot.builder;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.InventoryBuilder;
import de.exceptionflug.mccommons.inventories.api.InventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryType;
import de.exceptionflug.mccommons.inventories.api.InventoryWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class SpigotInventoryBuilder implements InventoryBuilder {

    private final Map<UUID, Map.Entry<InventoryWrapper, Long>> buildMap = new LinkedHashMap<>();
    private final List<InventoryWrapper> wrappers = new LinkedList<>();

    @Override
    public <T> T build(T prebuild, final InventoryWrapper wrapper) {
        boolean reopen = false;
        boolean register = prebuild == null;
        if(prebuild instanceof Inventory) {
            final Inventory inventory = (Inventory) prebuild;
            if(!inventory.getTitle().equals(wrapper.getTitle()) || inventory.getSize() != wrapper.getSize() || inventory.getType() != Converters.convert(wrapper.getInventoryType(), org.bukkit.event.inventory.InventoryType.class)) {
                if(wrapper.getInventoryType().isChest()) {
                    prebuild = (T) Bukkit.createInventory((InventoryHolder) wrapper.getPlayer(), wrapper.getSize(), wrapper.getTitle());
                } else {
                    prebuild = (T) Bukkit.createInventory((InventoryHolder) wrapper.getPlayer(), Converters.convert(wrapper.getInventoryType(), org.bukkit.event.inventory.InventoryType.class), wrapper.getTitle());
                }
                reopen = true;
            }
        } else {
            if(wrapper.getInventoryType().isChest()) {
                prebuild = (T) Bukkit.createInventory((InventoryHolder) wrapper.getPlayer(), wrapper.getSize(), wrapper.getTitle());
            } else {
                prebuild = (T) Bukkit.createInventory((InventoryHolder) wrapper.getPlayer(), Converters.convert(wrapper.getInventoryType(), org.bukkit.event.inventory.InventoryType.class), wrapper.getTitle());
            }
        }
        final Inventory inventory = (Inventory) prebuild;
        for (int i = 0; i < wrapper.getSize(); i++) {
            final InventoryItem item = (InventoryItem) wrapper.getInventoryItemMap().get(i);
            final ItemStack currentStack = inventory.getItem(i);
            if(item == null) {
                if(currentStack != null) {
                    inventory.setItem(i, null);
                }
            }
            if(item != null) {
                if(currentStack == null) {
                    if(item.getItemStackWrapper() == null) {
                        Bukkit.getLogger().severe("InventoryItem's ItemStack is null @ slot "+i);
                        continue;
                    }
                    inventory.setItem(i, Converters.convert(item.getItemStackWrapper(), ItemStack.class));
                } else {
                    if(item.getItemStackWrapper().getType() == ItemType.PLAYER_HEAD && currentStack.getType() == Material.SKULL_ITEM) {
                        final SkullMeta meta1 = (SkullMeta) ((ItemStack)item.getItemStackWrapper().getHandle()).getItemMeta();
                        final SkullMeta meta2 = (SkullMeta) currentStack.getItemMeta();
                        if(meta1.hasOwner() && meta2.hasOwner() && meta1.getOwner().equals(meta2.getOwner())) {
                            continue;
                        }
                    }
                    if(!currentStack.equals(Converters.convert(item.getItemStackWrapper(), ItemStack.class))) {
                        inventory.setItem(i, Converters.convert(item.getItemStackWrapper(), ItemStack.class));
                    }
                }
            }
        }
        buildMap.put(((Player)wrapper.getPlayer()).getUniqueId(), new AbstractMap.SimpleEntry<>(wrapper, System.currentTimeMillis()));
        if(register) {
            Bukkit.broadcastMessage("REGISTER "+inventory.getTitle());
            wrappers.add(wrapper);
        }
        if(reopen)
            ((Player) wrapper.getPlayer()).openInventory(inventory);
        return prebuild;
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
        wrappers.removeIf(wrapper -> {
            if(((Player)wrapper.getPlayer()).getUniqueId().equals(uniqueId)) {
                wrapper.markAsUnregistered();
                return true;
            }
            return false;
        });
        buildMap.remove(uniqueId);
    }

    @Override
    public Map.Entry<InventoryWrapper, Long> getLastBuild(final UUID uniqueId) {
        return buildMap.get(uniqueId);
    }

    @Override
    public void uncache(final InventoryWrapper wrapper) {
        wrapper.markAsUnregistered();
        wrappers.remove(wrapper);
    }

    @Override
    public void open(final InventoryWrapper wrapper) {
        ((Player)wrapper.getPlayer()).openInventory((Inventory) wrapper.build());
    }

}
