package de.exceptionflug.mccommons.holograms.line;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.Location;

import java.util.*;

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
        return 0.2;
    }

    @Override
    protected List<PacketContainer> createDespawnPackets() {
        final PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        container.getIntegerArrays().write(0, new int[] {getEntityID()});
        return Collections.singletonList(container);
    }

    @Override
    protected List<PacketContainer> createSpawnPackets() {
        final PacketContainer spawnEntityLiving = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawnEntityLiving.getIntegers().write(0, getEntityID());
        spawnEntityLiving.getUUIDs().write(0, UUID.randomUUID());
        spawnEntityLiving.getIntegers().write(1, 1);
        spawnEntityLiving.getDoubles().write(0, getLocation().getX());
        spawnEntityLiving.getDoubles().write(1, getLocation().getY()-2);
        spawnEntityLiving.getDoubles().write(2, getLocation().getZ());
        spawnEntityLiving.getDataWatcherModifier().write(0, buildMetadata());
        return Collections.singletonList(spawnEntityLiving);
    }

    @Override
    protected List<PacketContainer> createMetadataPackets() {
        final PacketContainer metadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadata.getIntegers().write(0, getEntityID());
        metadata.getWatchableCollectionModifier().write(0, buildMetadata().getWatchableObjects());
        return Collections.singletonList(metadata);
    }

    @Override
    protected List<PacketContainer> createTeleportPackets() {
        final PacketContainer teleport = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        teleport.getIntegers().write(0, getEntityID());
        teleport.getDoubles().write(0, getLocation().getX());
        teleport.getDoubles().write(1, getLocation().getY()-2);
        teleport.getDoubles().write(2, getLocation().getZ());
        return Collections.singletonList(teleport);
    }

    private WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher metadata = new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20),
                new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.ofNullable(WrappedChatComponent.fromText(text).getHandle())),
                new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true),
                new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true)));
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
