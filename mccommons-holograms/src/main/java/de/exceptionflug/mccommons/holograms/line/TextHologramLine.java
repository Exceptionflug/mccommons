package de.exceptionflug.mccommons.holograms.line;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

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
        final WrappedDataWatcher metadata = new WrappedDataWatcher();
        metadata.setObject(0, (byte) 0x20); // Make armor stand invisible
        metadata.setObject(2, text); // Set name tag
        metadata.setObject(3, (byte) 1); // Always show nametag
        metadata.setObject(10, (byte) 0); // Disable all default vals in bitmask
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
