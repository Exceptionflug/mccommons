package de.exceptionflug.mccommons.inventories.spigot.converters;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTOutputStream;
import de.exceptionflug.mccommons.core.Converter;
import de.exceptionflug.mccommons.inventories.spigot.utils.ReflectionUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class FlowNbtNmsNbtConverter implements Converter<CompoundTag, Object> {

    private static Method nbtCompressedStreamToolAMethod;

    static {
        try {
            nbtCompressedStreamToolAMethod = ReflectionUtil.getClass("{nms}.NBTCompressedStreamTools").getMethod("a", InputStream.class);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object convert(final CompoundTag src) {
        byte[] data = null;
        try(final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            new NBTOutputStream(byteArrayOutputStream).writeTag(src);
            data = byteArrayOutputStream.toByteArray();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            return nbtCompressedStreamToolAMethod.invoke(null, byteArrayInputStream);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
