package de.exceptionflug.mccommons.inventories.spigot.converters;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;

import java.io.*;
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
        try(final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            nbtCompressedStreamToolAMethod.invoke(null, src, byteArrayOutputStream);
            data = byteArrayOutputStream.toByteArray();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            return (CompoundTag) new NBTInputStream(byteArrayInputStream).readTag();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
