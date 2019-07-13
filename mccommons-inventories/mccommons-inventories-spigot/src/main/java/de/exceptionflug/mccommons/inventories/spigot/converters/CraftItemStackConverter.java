package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CraftItemStackConverter implements Converter<CraftItemStack, ItemStack> {

    @Override
    public ItemStack convert(final CraftItemStack src) {
        return new ItemStack(src);
    }

}
