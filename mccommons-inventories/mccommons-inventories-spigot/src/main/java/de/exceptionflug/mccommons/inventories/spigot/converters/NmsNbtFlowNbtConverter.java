package de.exceptionflug.mccommons.inventories.spigot.converters;

import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class NmsNbtFlowNbtConverter implements Converter<Object, CompoundTag> {

	private static Method nbtCompressedStreamToolAMethod;

	static {
		try {
			nbtCompressedStreamToolAMethod = ReflectionUtil.getClass("{nms}.NBTCompressedStreamTools").getMethod("a", ReflectionUtil.getClass("{nms}.NBTTagCompound"), OutputStream.class);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public CompoundTag convert(final Object src) {
		byte[] data = null;
		try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			nbtCompressedStreamToolAMethod.invoke(null, src, byteArrayOutputStream);
			data = byteArrayOutputStream.toByteArray();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
			return (CompoundTag) new NBTInputStream(byteArrayInputStream).readTag(32).getTag();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
