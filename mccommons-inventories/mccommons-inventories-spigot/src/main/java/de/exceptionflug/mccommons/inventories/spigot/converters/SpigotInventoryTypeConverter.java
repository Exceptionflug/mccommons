package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.api.InventoryType;

public class SpigotInventoryTypeConverter implements Converter<InventoryType, org.bukkit.event.inventory.InventoryType> {

    @Override
    public org.bukkit.event.inventory.InventoryType convert(final InventoryType src) {
        switch (src) {

            case ANVIL:
                return org.bukkit.event.inventory.InventoryType.ANVIL;

            case BEACON:
                return org.bukkit.event.inventory.InventoryType.BEACON;

            case BREWING_STAND:
                return org.bukkit.event.inventory.InventoryType.BREWING;

            case CHEST:
                return org.bukkit.event.inventory.InventoryType.CHEST;

            case CRAFTING_TABLE:
                return org.bukkit.event.inventory.InventoryType.WORKBENCH;

            case DISPENSER:
                return org.bukkit.event.inventory.InventoryType.DISPENSER;

            case DROPPER:
                return org.bukkit.event.inventory.InventoryType.DROPPER;

            case ENCHANTMENT_TABLE:
                return org.bukkit.event.inventory.InventoryType.ENCHANTING;

            case FURNACE:
                return org.bukkit.event.inventory.InventoryType.FURNACE;

            case HOPPER:
                return org.bukkit.event.inventory.InventoryType.HOPPER;

            case VILLAGER:
                return org.bukkit.event.inventory.InventoryType.MERCHANT;

        }
        return null;
    }
}
