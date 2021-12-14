package de.exceptionflug.mccommons.inventories.proxy.utils;

import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.TextureProvider;
import de.exceptionflug.mccommons.core.utils.ProtocolVersions;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.data.ItemType;
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
		ItemStack[] a = inv.itemsIndexed(ProtocolVersions.MINECRAFT_1_8).toArray(new ItemStack[0]);
		int c = 0;
		for (ItemStack ic : a) {
			if (ic == null || ic.itemType() == ItemType.AIR) {
				c++;
			}
		}
		return c;
	}

	public static int getFirstSlot(final Inventory inv, final ItemStack is) {
		ItemStack[] a = inv.itemsIndexed(ProtocolVersions.MINECRAFT_1_8).toArray(new ItemStack[0]);
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
		is.lore(lore, true);
		is.displayName(dname);
		return is;
	}

	public static ItemStack removeLore(ItemStack is) {
		is.lore(new ArrayList<>(), true);
		return is;
	}

	public static ItemStack setSkullAndName(ItemStack is, String name) {
		is.itemType(ItemType.PLAYER_HEAD);
		setSkullOwner(is, name);
		return is;
	}

	public static int count(ProxiedPlayer p, ItemType material) {
		int r = 0;
		PlayerInventory inv = Protocolize.playerProvider().player(p.getUniqueId()).proxyInventory();
		for (ItemStack is : inv.itemsIndexed()) {
			if (is == null && material == ItemType.AIR) {
				r += 1;
			}
			if (is != null && is.itemType().equals(material))
				r += is.amount();
		}
		return r;
	}

	public static int count(ProxiedPlayer p, Predicate<ItemStack> match) {
		int r = 0;
		PlayerInventory inv = Protocolize.playerProvider().player(p.getUniqueId()).proxyInventory();
		for (ItemStack is : inv.itemsIndexed()) {
			if (is != null && match.test(is)) {
				r += is.amount();
			}
		}
		return r;
	}

	public static ItemStack addGlow(ItemStack item) {
		item.nbtData().put("ench", new ListTag<>(CompoundTag.class));
		return item;
	}

	public static ItemStack unbreakable(ItemStack is) {
		is.nbtData().put("Unbreakable", new ByteTag((byte) 1));
		return is;
	}

	public static ItemStack removeGlow(ItemStack is) {
		is.nbtData().remove("ench");
		return is;
	}

	public static ItemStack first(Inventory inv, Predicate<ItemStack> match) {
		for (ItemStack is : inv.itemsIndexed(ProtocolVersions.MINECRAFT_1_8)) {
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
		final CompoundTag skullOwner = (CompoundTag) ((CompoundTag) stack.nbtData())
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
		stack.nbtData().put("SkullOwner", skullOwner);
		return stack;
	}

	public static void setSkullOwner(final ItemStack stack, final String skullOwner) {
		if (!Providers.has(TextureProvider.class))
			stack.nbtData().put("SkullOwner", new StringTag(skullOwner));
		else
			setSkullTexture(stack, Providers.get(TextureProvider.class).getSkin(skullOwner));
	}

	public static String getSkullOwner(final ItemStack stack) {
		if (stack.nbtData().containsKey("SkullOwner")) {
			final Tag t = stack.nbtData().get("SkullOwner");
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
