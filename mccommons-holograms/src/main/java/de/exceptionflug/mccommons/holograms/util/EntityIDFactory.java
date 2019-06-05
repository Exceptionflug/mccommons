package de.exceptionflug.mccommons.holograms.util;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityIDFactory {

    private static final FieldAccessor ENTITY_ID = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);

    public static int getAndIncrement() {
        final AtomicInteger out = (AtomicInteger) ENTITY_ID.get(null);
        final int i = out.incrementAndGet();
        ENTITY_ID.set(null, out);
        return i;
    }

}
