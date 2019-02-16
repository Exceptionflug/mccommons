package de.exceptionflug.mccommons.holograms;

import de.exceptionflug.mccommons.core.utils.ConcurrentLinkedList;
import de.exceptionflug.mccommons.holograms.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class Holograms {

    public static double hologramTrackingDistance = 32;
    final static List<Hologram> KNOWN_HOLOGRAMS = new ConcurrentLinkedList<>();

    private static JavaPlugin mccommons;

    public static Hologram createHologram(final Location location) {
        return new HologramImpl(location);
    }

    public static void init(final JavaPlugin plugin) {
        mccommons = plugin;
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), mccommons);
    }

    public static List<Hologram> getKnownHolograms() {
        return Collections.unmodifiableList(KNOWN_HOLOGRAMS);
    }

    public static List<Hologram> getHologramsInWorld(final World world) {
        final List<Hologram> out = new ArrayList<>();
        for (final Hologram hologram : KNOWN_HOLOGRAMS) {
            if (hologram.getLocation().getWorld().equals(world))
                out.add(hologram);
        }
        return out;
    }

    public static JavaPlugin getRegistrant() {
        return mccommons;
    }

    public static void reload() {
        KNOWN_HOLOGRAMS.forEach(Hologram::respawn);
    }
}
