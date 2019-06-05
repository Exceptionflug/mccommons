package de.exceptionflug.mccommons.holograms.line;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import de.exceptionflug.mccommons.holograms.util.EntityIDFactory;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemHologramLine extends AbstractHologramLine {

    private ItemStack itemStack;
    private int vehicleId;

    public ItemHologramLine(final ItemStack itemStack) {
        this.itemStack = itemStack;
        vehicleId = EntityIDFactory.getAndIncrement();
    }

    public ItemHologramLine(final Location location, final ItemStack stack) {
        super(location);
        this.itemStack = stack;
        vehicleId = EntityIDFactory.getAndIncrement();
    }

    @Override
    public double getHeight() {
        return 0.2;
    }

    protected List<PacketContainer> createDespawnPackets() {
        final PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        container.getIntegerArrays().write(0, new int[] {getEntityID(), vehicleId});
        return Collections.singletonList(container);
    }

    protected List<PacketContainer> createSpawnPackets() {
//        final WrapperPlayServerSpawnEntity spawnEntity = new WrapperPlayServerSpawnEntity();
//        spawnEntity.setEntityID(getEntityID());
//        spawnEntity.setType(2);
//        spawnEntity.setObjectData(0);
//        spawnEntity.setOptionalSpeedX(0);
//        spawnEntity.setOptionalSpeedY(0);
//        spawnEntity.setOptionalSpeedZ(0);
//        spawnEntity.setX(getLocation().getX());
//        spawnEntity.setY(getLocation().getY());
//        spawnEntity.setZ(getLocation().getZ());

        // Cannot use protocollib here cause EntityTypes field cannot be accessed using protocollib
        final PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(
                getEntityID(),
                UUID.randomUUID(),
                getLocation().getX(),
                getLocation().getY(),
                getLocation().getZ(),
                0,
                0,
                EntityTypes.ITEM,
                0,
                new Vec3D(0,0,0));

        final PacketContainer spawnVehicle = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawnVehicle.getIntegers().write(0, vehicleId);
        spawnVehicle.getUUIDs().write(0, UUID.randomUUID());
        spawnVehicle.getIntegers().write(1, 1);
        spawnVehicle.getDoubles().write(0, getLocation().getX());
        spawnVehicle.getDoubles().write(1, getLocation().getY()+0.48);
        spawnVehicle.getDoubles().write(2, getLocation().getZ());
        spawnVehicle.getDataWatcherModifier().write(0, buildMetadataVehicle());

//        final WrapperPlayServerSpawnEntityLiving spawnVehicle = new WrapperPlayServerSpawnEntityLiving();
//        spawnVehicle.setEntityID(vehicleId);
//        spawnVehicle.setType(EntityType.ARMOR_STAND);
//        spawnVehicle.setX(getLocation().getX());
//        spawnVehicle.setY(getLocation().getY()+0.48);
//        spawnVehicle.setZ(getLocation().getZ());
//        spawnVehicle.setMetadata(buildMetadataVehicle());

        return Arrays.asList(new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY, spawnEntity), spawnVehicle);
    }

    @Override
    protected List<PacketContainer> createTeleportPackets() {
        final PacketContainer detachEntity = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
        detachEntity.getIntegers().write(0, getEntityID());
        detachEntity.getIntegers().write(1, -1);

        final PacketContainer teleport = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        teleport.getIntegers().write(0, getEntityID());
        teleport.getDoubles().write(0, getLocation().getX());
        teleport.getDoubles().write(1, getLocation().getY()-2);
        teleport.getDoubles().write(2, getLocation().getZ());

        final PacketContainer teleportVehicle = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportVehicle.getIntegers().write(0, vehicleId);
        teleportVehicle.getDoubles().write(0, getLocation().getX());
        teleportVehicle.getDoubles().write(1, getLocation().getY()-2+0.48);
        teleportVehicle.getDoubles().write(2, getLocation().getZ());

        final PacketContainer attachEntity = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
        attachEntity.getIntegers().write(0, getEntityID());
        attachEntity.getIntegers().write(1, vehicleId);
        return Arrays.asList(detachEntity, teleport, teleportVehicle, attachEntity);
    }

    protected List<PacketContainer> createMetadataPackets() {
        final PacketContainer metadataVehicle = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadataVehicle.getIntegers().write(0, vehicleId);
        metadataVehicle.getWatchableCollectionModifier().write(0, buildMetadataVehicle().getWatchableObjects());

        final PacketContainer attach = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
        attach.getIntegers().write(0, getEntityID());
        attach.getIntegers().write(1, vehicleId);

        final PacketContainer metadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadata.getIntegers().write(0, getEntityID());
        metadata.getWatchableCollectionModifier().write(0, buildMetadata().getWatchableObjects());
        return Arrays.asList(metadataVehicle, metadata, attach);
    }

    private WrappedDataWatcher buildMetadataVehicle() {
        return new WrappedDataWatcher(Collections.singletonList(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20)));
    }

    private WrappedDataWatcher buildMetadata() {
        return new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x04),
                new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, WrappedDataWatcher.Registry.getItemStackSerializer(false)), itemStack)));
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
                "itemStack=" + itemStack +
                ", vehicleId=" + vehicleId +
                '}';
    }
}
