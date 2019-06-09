package de.exceptionflug.mccommons.inventories.spigot.utils;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.StringTag;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.TextureProvider;
import de.exceptionflug.mccommons.inventories.spigot.item.SpigotItemStackWrapper;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Predicate;

/*
This class is definitely not stolen from xkuyax
 */
public class ItemUtils {


    private static Field profileField;

    static {
        try {
            final Class craftMetaSkull = Class.forName("org.bukkit.craftbukkit."+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]+".inventory.CraftMetaSkull");
            profileField = craftMetaSkull.getDeclaredField("profile");
            profileField.setAccessible(true);
        } catch (final ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    
    public static String getHiddenString(final ItemStack item) {
        // Only the color chars at the end of the string is it
        if (!item.hasItemMeta()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (!item.getItemMeta().hasDisplayName()) return null;
        final char[] chars = item.getItemMeta().getDisplayName().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == ChatColor.COLOR_CHAR) continue;
            if (i + 1 < chars.length) {
                if (chars[i + 1] == ChatColor.COLOR_CHAR && i > 1 && chars[i - 1] == ChatColor.COLOR_CHAR) builder.append(c);
                else if (builder.length() > 0) builder = new StringBuilder();
            } else if (i > 0 && chars[i - 1] == ChatColor.COLOR_CHAR) builder.append(c);
        }
        if (builder.length() == 0) return null;
        return builder.toString();
    }
    
    public static ItemStack setHiddenString(final ItemStack itemToName, final String name) {
        StringBuilder itemName;
        ItemMeta meta = itemToName.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(itemToName.getType());
            meta.setDisplayName(itemToName.getType().name());
        }
        itemName = new StringBuilder(meta.getDisplayName());
        for (int i = 0; i < name.length(); i++) {
            itemName.append(ChatColor.COLOR_CHAR).append(name, i, i + 1);
        }
        meta.setDisplayName(itemName.toString());
        itemToName.setItemMeta(meta);
        return itemToName;
    }
    
    private static String toReadable(final String string) {
        final String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
        }
        return (org.apache.commons.lang.StringUtils.join(names, " "));
    }
    
    public static boolean hasHiddenString(final ItemStack item) {
        return getHiddenString(item) != null;
    }
    
    public static int getFreeSlots(final Inventory inv) {
        ItemStack[] a = inv.getContents();
        int c = 0;
        for (ItemStack ic : a) {
            if (ic == null || ic.getType() == Material.AIR) {
                c++;
            }
        }
        return c;
    }
    
    public static int getFirstSlot(final Inventory inv, final ItemStack is) {
        ItemStack[] a = inv.getContents();
        for (int i = 0; i < a.length; i++) {
            ItemStack ic = a[i];
            if (ic != null && is.equals(ic)) {
                return i;
            }
        }
        return -1;
    }
    
    public static int getFirstSlot(final Inventory inv, final String unique) {
        ItemStack[] a = inv.getContents();
        for (int i = 0; i < a.length; i++) {
            ItemStack ic = a[i];
            if (ic != null) {
                String n = getHiddenString(ic);
                if (n != null && n.equalsIgnoreCase(unique)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static ItemStack removeLore(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        im.setLore(new ArrayList<>());
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack setSkullAndName(ItemStack is, String name) {
        is.setType(Material.SKULL_ITEM);
        is.setDurability((short) 3);
        setSkullOwner(is, name);
        return is;
    }

    public static void setSkullOwner(final ItemStack stack, final String skullOwner) {
        if(!Providers.has(TextureProvider.class)) {
            final SkullMeta itemMeta = (SkullMeta) stack.getItemMeta();
            itemMeta.setOwner(skullOwner);
            stack.setItemMeta(itemMeta);
        } else
            setSkullTexture(stack, Providers.get(TextureProvider.class).getSkin(skullOwner));
    }
    
    public static int count(Inventory inv, String hidden) {
        int r = 0;
        for (ItemStack is : inv.getContents()) {
            if (is != null) if (hidden.equalsIgnoreCase(getHiddenString(is))) r += is.getAmount();
        }
        return r;
    }
    
    public static int count(Player p, Material material) {
        int r = 0;
        Inventory inv = p.getInventory();
        for (ItemStack is : inv.getContents()) {
            if (is == null && material == Material.AIR) {
                r += 1;
            }
            if (is != null && is.getType().equals(material)) r += is.getAmount();
        }
        return r;
    }
    
    public static int count(Player p, Predicate<ItemStack> match) {
        int r = 0;
        Inventory inv = p.getInventory();
        for (ItemStack is : inv.getContents()) {
            if (is != null && match.test(is)) {
                r += is.getAmount();
            }
        }
        return r;
    }
    
    public static int count(Player p, String hidden) {
        return count(p.getInventory(), hidden);
    }
    
    public static void remove(Player p, int toRemove, Material material) {
        Inventory inv = p.getInventory();
        int removed = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is == null || !is.getType().equals(material)) {
                continue;
            }
            int a = is.getAmount();
            while (a > 0 && removed < toRemove) {
                removed++;
                a--;
            }
            if (a == 0) {
                inv.clear(i);
            } else {
                is.setAmount(a);
            }
        }
        p.updateInventory();
    }
    
    public static void remove(Player p, int toRemove, Predicate<ItemStack> match) {
        Inventory inv = p.getInventory();
        int removed = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is == null || !match.test(is)) {
                continue;
            }
            int a = is.getAmount();
            while (a > 0 && removed < toRemove) {
                removed++;
                a--;
            }
            if (a == 0) {
                inv.clear(i);
            } else {
                is.setAmount(a);
            }
        }
        p.updateInventory();
    }
    
    public static void remove(Player p, String hidden) {
        Inventory inv = p.getInventory();
        for (ItemStack is : inv) {
            String s = is != null && is.getType() != Material.AIR ? getHiddenString(is) : null;
            if (s != null && s.equalsIgnoreCase(hidden)) {
                inv.remove(is);
            }
        }
    }
    
    public static void remove(Player p, String hidden, int toRemove) {
        Inventory inv = p.getInventory();
        int removed = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is == null) {
                continue;
            }
            String h = getHiddenString(is);
            if (hidden.equals(h)) {
                int a = is.getAmount();
                while (a > 0 && removed < toRemove) {
                    removed++;
                    a--;
                }
                if (a == 0) {
                    inv.clear(i);
                } else {
                    is.setAmount(a);
                }
            }
        }
        p.updateInventory();
    }
    
    public static ItemStack setNBTTags(ItemStack item, NBTTagPair... values) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) {
            tag = nmsStack.getTag();
        }
        NBTTagCompound finalTag = tag;
        Arrays.stream(values).forEach(nbtTagPair -> finalTag.set(nbtTagPair.getKey(), nbtTagPair.getValue()));
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
    
    public static ItemStack addGlow(ItemStack item) {
        return setNBTTags(item, new NBTTagPair("ench", new NBTTagList()));
    }
    
    public static ItemStack unbreakable(ItemStack is) {
        return setNBTTags(is, new NBTTagPair("Unbreakable", new NBTTagByte((byte) 1)));
    }
    
    public static ItemStack removeGlow(ItemStack is) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        tag.remove("Unbreakable");
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
    
    public static ItemStack first(Inventory inv, Predicate<ItemStack> match) {
        for (ItemStack is : inv.getContents()) {
            if (is != null && match.test(is)) {
                return is;
            }
        }
        return null;
    }
    
    public static ItemStack first(Inventory inv, Material mat) {
        int slot = inv.first(mat);
        if (slot == -1) {
            return null;
        }
        return inv.getItem(slot);
    }


    public static ItemStack setSkullTexture(final ItemStack stack, final String textureHash) {
        Preconditions.checkNotNull(stack, "The stack cannot be null!");
        Preconditions.checkNotNull(textureHash, "The textureHash cannot be null!");
        Preconditions.checkArgument(!textureHash.isEmpty(), "The textureHash cannot be empty!");
        final ItemMeta meta = stack.getItemMeta();
        Preconditions.checkState(meta instanceof SkullMeta, "Meta must be a skull meta");
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", textureHash));
        try {
            profileField.set(meta, profile);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        stack.setItemMeta(meta);
        return stack;
    }


    public static class NBTTagPair {
        
        private final String key;
        private final NBTBase value;

        public NBTTagPair(final String key, final NBTBase value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public NBTBase getValue() {
            return value;
        }
    }
    
}
