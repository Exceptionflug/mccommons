package de.exceptionflug.mccommons.inventories.spigot.converters;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import de.exceptionflug.mccommons.core.Converter;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FlowNbtNmsNbtConverter implements Converter<CompoundTag, NBTTagCompound> {

    @Override
    public NBTTagCompound convert(final CompoundTag src) {
        byte[] data = null;
        try(final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            new NBTOutputStream(byteArrayOutputStream).writeTag(src);
            data = byteArrayOutputStream.toByteArray();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            return NBTCompressedStreamTools.a(byteArrayInputStream);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
