package de.exceptionflug.mccommons.holograms.util;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;

public class EntityIDFactory {

    private static final FieldAccessor ENTITY_ID = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);

    public static int getAndIncrement() {
        final int out = (int) ENTITY_ID.get(null);
        ENTITY_ID.set(null, out+1);
        return out;
    }

}
