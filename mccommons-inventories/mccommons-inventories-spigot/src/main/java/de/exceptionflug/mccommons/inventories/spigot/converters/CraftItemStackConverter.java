package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import org.bukkit.inventory.ItemStack;

public class CraftItemStackConverter implements Converter<Object, ItemStack> {

    @Override
    public ItemStack convert(final Object src) {
        return new ItemStack((ItemStack) src);
    }

}
