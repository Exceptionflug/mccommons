package de.exceptionflug.mccommons.inventories.spigot.converters;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import de.exceptionflug.mccommons.core.Converter;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NmsNbtFlowNbtConverter implements Converter<NBTTagCompound, CompoundTag> {

    @Override
    public CompoundTag convert(final NBTTagCompound src) {
        byte[] data = null;
        try(final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            NBTCompressedStreamTools.a(src, byteArrayOutputStream);
            data = byteArrayOutputStream.toByteArray();
        } catch (final IOException e) {
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
