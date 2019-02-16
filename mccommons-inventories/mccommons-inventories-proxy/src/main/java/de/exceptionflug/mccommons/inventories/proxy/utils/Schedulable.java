package de.exceptionflug.mccommons.inventories.proxy.utils;

import de.exceptionflug.mccommons.core.Providers;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface Schedulable {

    default void async(final Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(Providers.get(Plugin.class), runnable);
    }

    default void later(final Runnable runnable, final long time, final TimeUnit unit) {
        ProxyServer.getInstance().getScheduler().schedule(Providers.get(Plugin.class), runnable, time, unit);
    }

    default void repeat(final Consumer<Integer> runnable, final long tickPeriod, final TimeUnit unit, final int times) {
        repeat(runnable, tickPeriod, tickPeriod, unit, times);
    }

    default void repeat(final Consumer<Integer> runnable, final long delay, final long tickPeriod, final TimeUnit unit, final int times) {
        final AtomicInteger count = new AtomicInteger();
        final ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(Providers.get(Plugin.class), new Runnable() {

            @Override
            public void run() {
                count.getAndIncrement();
                if(count.get() <= times)
                    runnable.accept(count.get());
            }
        }, delay, tickPeriod, unit);
        async(() -> {
            while (count.get() <= times) {
                try {
                    Thread.sleep(tickPeriod);
                } catch (final Exception e) {
                }
            }
            task.cancel();
        });
    }

}
