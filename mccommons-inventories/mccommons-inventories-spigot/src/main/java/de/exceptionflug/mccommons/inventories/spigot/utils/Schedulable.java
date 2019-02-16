package de.exceptionflug.mccommons.inventories.spigot.utils;

import de.exceptionflug.mccommons.core.Providers;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public interface Schedulable {

    default void sync(final Runnable runnable) {
        Bukkit.getScheduler().runTask(Providers.get(JavaPlugin.class), runnable);
    }

    default void async(final Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Providers.get(JavaPlugin.class), runnable);
    }

    default void later(final Runnable runnable, final long ticks) {
        Bukkit.getScheduler().runTaskLater(Providers.get(JavaPlugin.class), runnable, ticks);
    }

    default void laterAsync(final Runnable runnable, final long ticks) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Providers.get(JavaPlugin.class), runnable, ticks);
    }

    default void repeat(final Consumer<Integer> runnable, final long tickPeriod, final int times) {
        repeat(runnable, tickPeriod, tickPeriod, times);
    }

    default void repeat(final Consumer<Integer> runnable, final long delay, final long tickPeriod, final int times) {
        new BukkitRunnable() {
            private int count;
            @Override
            public void run() {
                count ++;
                if(count == times)
                    cancel();
                runnable.accept(count);
            }
        }.runTaskTimer(Providers.get(JavaPlugin.class), delay, tickPeriod);
    }

    default void repeatAsync(final Consumer<Integer> runnable, final long tickPeriod, final int times) {
        repeatAsync(runnable, tickPeriod, tickPeriod, times);
    }

    default void repeatAsync(final Consumer<Integer> runnable, final long delay, final long tickPeriod, final int times) {
        new BukkitRunnable() {
            private int count;
            @Override
            public void run() {
                count ++;
                if(count == times)
                    cancel();
                runnable.accept(count);
            }
        }.runTaskTimerAsynchronously(Providers.get(JavaPlugin.class), delay, tickPeriod);
    }

}
