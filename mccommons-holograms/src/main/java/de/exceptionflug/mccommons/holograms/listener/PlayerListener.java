package de.exceptionflug.mccommons.holograms.listener;

import de.exceptionflug.mccommons.holograms.Hologram;
import de.exceptionflug.mccommons.holograms.Holograms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayerListener implements Listener {

	private final JavaPlugin plugin;

	public PlayerListener(JavaPlugin mccommons) {
		this.plugin = mccommons;
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			Holograms.getHologramsInWorld(p.getWorld()).forEach(it -> it.spawnFor(p));
		});
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		Holograms.getHologramsInWorld(p.getWorld()).forEach(it -> it.despawnFor(p));
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent e) {
		final Player p = e.getPlayer();
		if (!e.getFrom().getWorld().equals(e.getTo().getWorld())) {
			Holograms.getHologramsInWorld(e.getFrom().getWorld()).forEach(it -> it.despawnFor(p));
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
				Holograms.getHologramsInWorld(e.getTo().getWorld()).forEach(it -> it.spawnFor(p));
			}, 1);
		} else {
			processLocationUpdate(p, e.getTo(), e.getFrom());
		}
	}

	private void processLocationUpdate(final Player p, final Location to, final Location from) {
		final List<Hologram> hologramList = Holograms.getHologramsInWorld(p.getWorld());
		for (final Hologram hologram : hologramList) {
			if (hologram.isDespawned())
				continue;
			final double distanceNow = hologram.getLocation().distance(to);
			final double previousDistance = hologram.getLocation().distance(from);
			if (distanceNow > Holograms.hologramTrackingDistance && previousDistance < Holograms.hologramTrackingDistance) {
				hologram.despawnFor(p);
			} else if (distanceNow < Holograms.hologramTrackingDistance && previousDistance > Holograms.hologramTrackingDistance) {
				Bukkit.getScheduler().runTaskAsynchronously(Holograms.getRegistrant(), () -> hologram.spawnFor(p));
			}
		}
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		processLocationUpdate(p, e.getTo(), e.getFrom());
	}

}
