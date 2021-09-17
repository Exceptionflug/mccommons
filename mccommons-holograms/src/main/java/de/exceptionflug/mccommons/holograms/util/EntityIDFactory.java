package de.exceptionflug.mccommons.holograms.util;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityIDFactory {

//	private static final FieldAccessor ENTITY_ID = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);

	private static final AtomicInteger ENTITY_ID = new AtomicInteger(100000);

	public static int getAndIncrement() {
		return ENTITY_ID.getAndIncrement();
	}

}
