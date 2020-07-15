package de.exceptionflug.mccommons.inventories.proxy.utils;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.TextureProvider;
import de.exceptionflug.mccommons.core.utils.ProtocolVersions;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.querz.nbt.tag.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/*
This class is definitely not stolen from xkuyax
 */
public class ItemUtils {

	private static String toReadable(final String string) {
		final String[] names = string.split("_");
		for (int i = 0; i < names.length; i++) {
			names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
		}
		return (org.apache.commons.lang.StringUtils.join(names, " "));
	}

	public static int getFreeSlots(final Inventory inv) {
		ItemStack[] a = inv.getItemsIndexed(ProtocolVersions.MINECRAFT_1_8).toArray(new ItemStack[0]);
		int c = 0;
		for (ItemStack ic : a) {
			if (ic == null || ic.getType() == ItemType.AIR) {
				c++;
			}
		}
		return c;
	}

	public static int getFirstSlot(final Inventory inv, final ItemStack is) {
		ItemStack[] a = inv.getItemsIndexed(ProtocolVersions.MINECRAFT_1_8).toArray(new ItemStack[0]);
		for (int i = 0; i < a.length; i++) {
			ItemStack ic = a[i];
			if (is.equals(ic)) {
				return i;
			}
		}
		return -1;
	}

	public static ItemStack setLoreAndDisplay(
		final ItemStack is,
		final List<String> lore,
		final String dname) {
		is.setLore(lore);
		is.setDisplayName(dname);
		return is;
	}

	public static ItemStack removeLore(ItemStack is) {
		is.setLore(new ArrayList<>());
		return is;
	}

	public static ItemStack setSkullAndName(ItemStack is, String name) {
		is.setType(ItemType.PLAYER_HEAD);
		setSkullOwner(is, name);
		return is;
	}

	public static int count(ProxiedPlayer p, ItemType material) {
		int r = 0;
		Inventory inv = InventoryModule.getInventory(p.getUniqueId(), 0);
		for (ItemStack is : inv.getItemsIndexed(ReflectionUtil.getProtocolVersion(p))) {
			if (is == null && material == ItemType.AIR) {
				r += 1;
			}
			if (is != null && is.getType().equals(material))
				r += is.getAmount();
		}
		return r;
	}

	public static int count(ProxiedPlayer p, Predicate<ItemStack> match) {
		int r = 0;
		Inventory inv = InventoryModule.getInventory(p.getUniqueId(), 0);
		for (ItemStack is : inv.getItemsIndexed(ReflectionUtil.getProtocolVersion(p))) {
			if (is != null && match.test(is)) {
				r += is.getAmount();
			}
		}
		return r;
	}

	public static ItemStack addGlow(ItemStack item) {
		((CompoundTag) item.getNBTTag()).put("ench", new ListTag<>(CompoundTag.class));
		return item;
	}

	public static ItemStack unbreakable(ItemStack is) {
		((CompoundTag) is.getNBTTag()).put("Unbreakable", new ByteTag((byte) 1));
		return is;
	}

	public static ItemStack removeGlow(ItemStack is) {
		((CompoundTag) is.getNBTTag()).remove("ench");
		return is;
	}

	public static ItemStack first(Inventory inv, Predicate<ItemStack> match) {
		for (ItemStack is : inv.getItemsIndexed(ProtocolVersions.MINECRAFT_1_8)) {
			if (is != null && match.test(is)) {
				return is;
			}
		}
		return null;
	}

	public static ItemStack setSkullTexture(final ItemStack stack, final String textureHash) {
		Preconditions.checkNotNull(stack, "The stack cannot be null!");
		Preconditions.checkNotNull(textureHash, "The textureHash cannot be null!");
		Preconditions.checkArgument(!textureHash.isEmpty(), "The textureHash cannot be empty!");
		final CompoundTag skullOwner = (CompoundTag) ((CompoundTag) stack.getNBTTag())
			.get("SkullOwner");
		skullOwner.put("Name", new StringTag(textureHash));
		final CompoundTag properties = (CompoundTag) skullOwner
			.get("Properties"/*, new CompoundTag()*/);
		final CompoundTag texture = new CompoundTag();
		texture.put("Value", new StringTag(textureHash));
		final ListTag<CompoundTag> textures = new ListTag<>(
			CompoundTag.class);
		properties.put("textures", textures);
		skullOwner.put("properties", properties);
		((CompoundTag) stack.getNBTTag()).put("SkullOwner", skullOwner);
		return stack;
	}

	public static void setSkullOwner(final ItemStack stack, final String skullOwner) {
		if (!Providers.has(TextureProvider.class))
			((CompoundTag) stack.getNBTTag()).put("SkullOwner", new StringTag(skullOwner));
		else
			setSkullTexture(stack, Providers.get(TextureProvider.class).getSkin(skullOwner));
	}

	public static String getSkullOwner(final ItemStack stack) {
		if (((CompoundTag) stack.getNBTTag()).containsKey("SkullOwner")) {
			final Tag t = ((CompoundTag) stack.getNBTTag()).get("SkullOwner");
			if (t instanceof StringTag) {
				return ((StringTag) t).getValue();
			} else if (t instanceof CompoundTag) {
				final CompoundTag skullOwner = (CompoundTag) t;
				final Tag t2 = skullOwner.get("Name");
				if (t2 instanceof StringTag) {
					return ((StringTag) t2).getValue();
				}
			}
		}
		return null;
	}

}
