package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import org.bukkit.event.inventory.ClickType;

public class SpigotClickTypeConverter implements Converter<ClickType, de.exceptionflug.mccommons.inventories.api.ClickType> {

	@Override
	public de.exceptionflug.mccommons.inventories.api.ClickType convert(final ClickType src) {
		switch (src) {

			case LEFT:
				return de.exceptionflug.mccommons.inventories.api.ClickType.LEFT_CLICK;

			case RIGHT:
				return de.exceptionflug.mccommons.inventories.api.ClickType.RIGHT_CLICK;

			case DROP:
				return de.exceptionflug.mccommons.inventories.api.ClickType.DROP;

			case MIDDLE:
				return de.exceptionflug.mccommons.inventories.api.ClickType.CREATIVE_MIDDLE_CLICK;

			case NUMBER_KEY:
				return de.exceptionflug.mccommons.inventories.api.ClickType.NUMBER_BUTTON_1;

			case SHIFT_LEFT:
				return de.exceptionflug.mccommons.inventories.api.ClickType.SHIFT_LEFT_CLICK;

			case SHIFT_RIGHT:
				return de.exceptionflug.mccommons.inventories.api.ClickType.SHIFT_RIGHT_CLICK;

			case CONTROL_DROP:
				return de.exceptionflug.mccommons.inventories.api.ClickType.DROP_ALL;

			case DOUBLE_CLICK:
				return de.exceptionflug.mccommons.inventories.api.ClickType.DOUBLE_CLICK;

			case WINDOW_BORDER_LEFT:
				return de.exceptionflug.mccommons.inventories.api.ClickType.LEFT_CLICK_OUTSIDE_INVENTORY_HOLDING_NOTHING;

			case WINDOW_BORDER_RIGHT:
				return de.exceptionflug.mccommons.inventories.api.ClickType.RIGHT_CLICK_OUTSIDE_INVENTORY_HOLDING_NOTHING;

		}
		return null;
	}
}
