package de.exceptionflug.mccommons.holograms.line;

import de.exceptionflug.mccommons.core.packetwrapper.WrapperPlayServerEntityDestroy;
import de.exceptionflug.mccommons.core.packetwrapper.WrapperPlayServerEntityMetadata;
import de.exceptionflug.mccommons.core.packetwrapper.WrapperPlayServerEntityTeleport;
import de.exceptionflug.mccommons.core.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;

import java.util.*;

public class TextHologramLine extends AbstractHologramLine {

	private final UUID uuid = UUID.randomUUID();
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
		destroy.setEntityIds(Arrays.asList(getEntityID()));
		return Collections.singletonList(destroy.getHandle());
	}

	@Override
	protected List<PacketContainer> createSpawnPackets() {
		final WrapperPlayServerSpawnEntityLiving spawnEntityLiving = new WrapperPlayServerSpawnEntityLiving();
		spawnEntityLiving.setEntityID(getEntityID());
		spawnEntityLiving.getHandle().getIntegers().write(1, 1); // Armor stand
		spawnEntityLiving.setX(getLocation().getX());
		spawnEntityLiving.setY(getLocation().getY());
		spawnEntityLiving.setZ(getLocation().getZ());
		spawnEntityLiving.setUniqueId(uuid);
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
		WrappedDataWatcher.Serializer componentSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
		WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
		WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), (byte) (0x20)); // Invisibility
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, componentSerializer), Optional.of(WrappedChatComponent.fromLegacyText(text).getHandle())); //CustomName
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), true); // CustomName-Visibility
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer), false); // Gravity
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, byteSerializer), (byte) 0x10); // Marker
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
