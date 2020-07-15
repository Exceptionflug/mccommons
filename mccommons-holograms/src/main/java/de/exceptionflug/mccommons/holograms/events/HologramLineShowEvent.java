package de.exceptionflug.mccommons.holograms.events;

import de.exceptionflug.mccommons.holograms.line.HologramLine;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class HologramLineShowEvent extends PlayerEvent implements Cancellable {

	private final HologramLine hologramLine;
	private boolean cancelled;

	public HologramLineShowEvent(final Player who, final HologramLine hologramLine) {
		super(who);
		this.hologramLine = hologramLine;
	}

	public HologramLine getHologramLine() {
		return hologramLine;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean b) {
		this.cancelled = b;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
