package de.exceptionflug.mccommons.holograms.line;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.exceptionflug.mccommons.core.packetwrapper.*;
import de.exceptionflug.mccommons.holograms.util.EntityIDFactory;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ItemHologramLine extends AbstractHologramLine {

	private final UUID uuid = UUID.randomUUID();
	private ItemStack itemStack;

	public ItemHologramLine(final ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemHologramLine(final Location location, final ItemStack stack) {
		super(location);
		this.itemStack = stack;
	}

	@Override
	public double getHeight() {
		return 0.5;
	}

	protected List<PacketContainer> createDespawnPackets() {
		final WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(Arrays.asList(getEntityID()));
		return Collections.singletonList(destroy.getHandle());
	}

	protected List<PacketContainer> createSpawnPackets() {
		final WrapperPlayServerSpawnEntity spawnEntity = new WrapperPlayServerSpawnEntity();
		spawnEntity.setEntityID(getEntityID());
//		spawnEntity.getHandle().getIntegers().write(1, 41); // Item
		spawnEntity.setX(getLocation().getX());
		spawnEntity.setY(getLocation().getY());
		spawnEntity.setZ(getLocation().getZ());
		spawnEntity.setUniqueId(uuid);
		spawnEntity.setType(EntityType.DROPPED_ITEM);
		return Arrays.asList(spawnEntity.getHandle());
	}

	@Override
	protected List<PacketContainer> createTeleportPackets() {
		final WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
		teleport.setEntityID(getEntityID());
		teleport.setX(getLocation().getX());
		teleport.setY(getLocation().getY());
		teleport.setZ(getLocation().getZ());
		return Arrays.asList(teleport.getHandle());
	}

	protected List<PacketContainer> createMetadataPackets() {
		final WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
		metadata.setEntityID(getEntityID());
		metadata.setMetadata(buildMetadata().getWatchableObjects());
		return Arrays.asList(metadata.getHandle());
	}

	private WrappedDataWatcher buildMetadata() {
		final WrappedDataWatcher metadata = new WrappedDataWatcher();
		WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
		WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
		WrappedDataWatcher.Serializer itemStackSerializer = WrappedDataWatcher.Registry.getItemStackSerializer(false);
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), false); // CustomName-Visibility
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer), true); // no Gravity
		metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, itemStackSerializer), itemStack); // Item
		return metadata;
	}

	public void setItemStack(final ItemStack itemStack) {
		this.itemStack = itemStack;
		update();
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public String toString() {
		return "ItemHologramLine{" +
			"itemStack=" + itemStack + '}';
	}
}
