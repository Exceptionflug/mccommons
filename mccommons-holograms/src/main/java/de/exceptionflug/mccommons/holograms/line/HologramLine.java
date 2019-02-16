package de.exceptionflug.mccommons.holograms.line;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface HologramLine {

    int getEntityID();
    void despawn();
    void despawnFor(final Player player);
    void spawn(final Location location);
    void spawnFor(final Location location, Player player);
    void spawnFor(final Player player);
    void update();
    void teleport(final Location location);
    Set<UUID> getViewers();
    Location getLocation();
    double getHeight();

}
