package de.exceptionflug.mccommons.inventories.spigot.builder;

import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.inventories.api.InventoryBuilder;
import de.exceptionflug.mccommons.inventories.api.InventoryItem;
import de.exceptionflug.mccommons.inventories.api.InventoryType;
import de.exceptionflug.mccommons.inventories.api.InventoryWrapper;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;
import net.minecraft.server.v1_14_R1.IInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class SpigotInventoryBuilder implements InventoryBuilder {

    private static Class minecraftInventoryClass;
    private static Field titleField;

    static {
        try {
            minecraftInventoryClass = ReflectionUtil.getClass("{obc}.inventory.CraftInventoryCustom$MinecraftInventory");
            titleField = minecraftInventoryClass.getDeclaredField("title");
            titleField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Map<UUID, Map.Entry<InventoryWrapper, Long>> buildMap = new LinkedHashMap<>();
    private final List<InventoryWrapper> wrappers = new LinkedList<>();

    @Override
    public <T> T build(T prebuild, final InventoryWrapper wrapper) {
        boolean reopen = false;
        boolean register = prebuild == null;
        if(prebuild instanceof Inventory) {
            final Inventory inventory = (Inventory) prebuild;
            if(!getTitle(inventory).equals(wrapper.getTitle()) || inventory.getSize() != wrapper.getSize() || inventory.getType() != Converters.convert(wrapper.getInventoryType(), org.bukkit.event.inventory.InventoryType.class)) {
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
                    if(item.getItemStackWrapper().getType() == ItemType.PLAYER_HEAD && currentStack.getType() == Material.PLAYER_HEAD) {
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
        if(register)
            wrappers.add(wrapper);
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
        wrappers.removeIf(wrapper -> ((Player)wrapper.getPlayer()).getUniqueId().equals(uniqueId));
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
        ((Player)wrapper.getPlayer()).openInventory((Inventory) wrapper.build());
    }

    private String getTitle(final Inventory inventory) {
        // Since the developers of spigot smoke weed every day, we can only get an inventory title
        // by using the InventoryView (which we don't have). So we use tricky and hacky reflections to get the title.

        final IInventory nmsInventory = ((CraftInventory)inventory).getInventory();
        if(!minecraftInventoryClass.isAssignableFrom(nmsInventory.getClass())) {
            throw new IllegalStateException("[MCCommons] Internal error: NMS Inventory can only be MinecraftInventory!");
        }
        try {
            return (String) titleField.get(nmsInventory);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
