package de.exceptionflug.mccommons.holograms.line;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TextHologramLine extends AbstractHologramLine {

    private String text;

    public TextHologramLine(final String text) {
        this.text = text;
    }

    public TextHologramLine(final String text, final Location location) {
        super(location);
        this.text = text;
    }

    @Override
    public double getHeight() {
        return 0.3;
    }

    @Override
    protected List<PacketContainer> createDespawnPackets() {
        final WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[] {getEntityID()});
        return Collections.singletonList(destroy.getHandle());
    }

    @Override
    protected List<PacketContainer> createSpawnPackets() {
        final WrapperPlayServerSpawnEntityLiving spawnEntityLiving = new WrapperPlayServerSpawnEntityLiving();
        spawnEntityLiving.setEntityID(getEntityID());
        spawnEntityLiving.setType(EntityType.ARMOR_STAND);
        spawnEntityLiving.setMetadata(buildMetadata());
        spawnEntityLiving.setX(getLocation().getX());
        spawnEntityLiving.setY(getLocation().getY());
        spawnEntityLiving.setZ(getLocation().getZ());
        return Collections.singletonList(spawnEntityLiving.getHandle());
    }

    @Override
    protected List<PacketContainer> createMetadataPackets() {
        final WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(getEntityID());
        metadata.setMetadata(buildMetadata().getWatchableObjects());
        return Collections.singletonList(metadata.getHandle());
    }

    @Override
    protected List<PacketContainer> createTeleportPackets() {
        final WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
        teleport.setEntityID(getEntityID());
        teleport.setX(getLocation().getX());
        teleport.setY(getLocation().getY());
        teleport.setZ(getLocation().getZ());
        return Collections.singletonList(teleport.getHandle());
    }

    private WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher metadata = new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20),
                new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(String.class)), text),
                new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 1),
                new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(10, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0)));
        return metadata;
    }

    public void setText(final String text) {
        this.text = text;
        update();
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "TextHologramLine{" +
                "text='" + text + '\'' +
                '}';
    }
}
