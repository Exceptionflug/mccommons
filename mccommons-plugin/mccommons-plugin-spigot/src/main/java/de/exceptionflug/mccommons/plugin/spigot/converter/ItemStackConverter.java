package de.exceptionflug.mccommons.plugin.spigot.converter;

import de.exceptionflug.mccommons.config.shared.ConfigItemStack;
import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.core.Converters;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.inventories.api.item.ItemType;
import de.exceptionflug.mccommons.inventories.spigot.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;

import java.util.List;
import java.util.stream.Collectors;

public class ItemStackConverter implements Converter<ConfigItemStack, ItemStack> {

    @Override
    public ItemStack convert(final ConfigItemStack src) {
        if(src == null)
            return null;
        ItemType type;
        try {
            type = ItemType.valueOf(src.getType());
        } catch (final IllegalArgumentException e) {
            Bukkit.getLogger().warning("[ItemStackConverter] WARN: "+src.getDisplayName()+" has invalid type "+src.getType());
            type = ItemType.GRASS;
        }
        final MaterialData convert = Converters.convert(type, MaterialData.class);
        ItemStack out = new ItemStack(convert.getItemType(), src.getAmount(), convert.getData() == 0 ? src.getDurability() : convert.getData());
        if(type == ItemType.PLAYER_HEAD) {
            String skullOwner = src.getSkull() == null ? "Exceptionflug" : src.getSkull();
            if(skullOwner.startsWith("texture:")) {
                final String[] spl = skullOwner.split(":");
                if(spl.length == 2) {
                    ItemUtils.setSkullTexture(out, spl[1]);
                }
            } else {
                ItemUtils.setSkullAndName(out, skullOwner);
            }
        }
        final ItemMeta meta = out.getItemMeta();
        meta.setLore(src.getLore().stream().map(FormatUtils::formatAmpersandColorCodes).collect(Collectors.toList()));
        meta.setDisplayName(src.getDisplayName());

        final List<String> flags = src.getFlags();
        for(final String flag : flags) {
            try {
                final ItemFlag spigotFlag = ItemFlag.valueOf(flag);
                meta.addItemFlags(spigotFlag);
            } catch (final IllegalArgumentException e) {
                Bukkit.getLogger().warning("[SpigotConfig] WARN: "+src.getDisplayName()+" has invalid item flag "+flag);
            }
        }

        out.setItemMeta(meta);

        for(final String enchantment : src.getEnchantments()) {
            final String[] spl = enchantment.split(":");
            if(spl.length != 2)
                continue;
            out.addUnsafeEnchantment(Enchantment.getByName(spl[0]), Integer.parseInt(spl[1]));
        }
        return out;
    }

}
