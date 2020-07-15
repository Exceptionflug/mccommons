package de.exceptionflug.mccommons.inventories.api.design;

import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.inventories.api.ActionHandler;
import de.exceptionflug.mccommons.inventories.api.CallResult;
import de.exceptionflug.mccommons.inventories.api.InventoryBuilder;
import de.exceptionflug.mccommons.inventories.api.InventoryType;

import java.util.Locale;

final class ChildInventoryWrapper<P, I, INV> extends AbstractBaseInventoryWrapper<P, I, INV> {

	private final AbstractBaseInventoryWrapper<P, I, INV> parent;

	protected ChildInventoryWrapper(final P player, final InventoryType type, final Locale locale, final ConfigWrapper config, final AbstractBaseInventoryWrapper<P, I, INV> parent) {
		super(player, type, locale);
		this.parent = parent;
		setTitle(config.getLocalizedString(locale, "Inventory", ".title", "&6Inventory"));
		registerActionHandler("noAction", click -> CallResult.DENY_GRABBING);
		registerActionHandlers();
	}

	public AbstractBaseInventoryWrapper<P, I, INV> getParent() {
		return parent;
	}

	@Override
	public ActionHandler getActionHandler(String name) {
		return parent.getActionHandler(name);
	}

	@Override
	public INV build() {
		final Object build = Providers.get(InventoryBuilder.class).build(parent.inventory, parent);
		parent.inventory = build;
		return (INV) build;
	}

	@Override
	public void updateInventory() {
	}

	@Override
	public void onExit(final boolean inventorySwitch) {
	}
}
