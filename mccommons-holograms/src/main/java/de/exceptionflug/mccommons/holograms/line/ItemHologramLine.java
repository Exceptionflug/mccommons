package de.exceptionflug.mccommons.holograms.line;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.exceptionflug.mccommons.holograms.util.EntityIDFactory;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        return 0.5;
    }

    protected List<PacketContainer> createDespawnPackets() {
        final WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[] {getEntityID(), vehicleId});
        return Collections.singletonList(destroy.getHandle());
    }

    protected List<PacketContainer> createSpawnPackets() {
        final WrapperPlayServerSpawnEntity spawnEntity = new WrapperPlayServerSpawnEntity();
        spawnEntity.setEntityID(getEntityID());
        spawnEntity.setType(2);
        spawnEntity.setObjectData(0);
        spawnEntity.setOptionalSpeedX(0);
        spawnEntity.setOptionalSpeedY(0);
        spawnEntity.setOptionalSpeedZ(0);
        spawnEntity.setX(getLocation().getX());
        spawnEntity.setY(getLocation().getY());
        spawnEntity.setZ(getLocation().getZ());

        final WrapperPlayServerSpawnEntityLiving spawnVehicle = new WrapperPlayServerSpawnEntityLiving();
        spawnVehicle.setEntityID(vehicleId);
        spawnVehicle.setType(EntityType.ARMOR_STAND);
        spawnVehicle.setX(getLocation().getX());
        spawnVehicle.setY(getLocation().getY()+0.48);
        spawnVehicle.setZ(getLocation().getZ());
        spawnVehicle.setMetadata(buildMetadataVehicle());

        return Arrays.asList(spawnEntity.getHandle(), spawnVehicle.getHandle());
    }

    @Override
    protected List<PacketContainer> createTeleportPackets() {
        final WrapperPlayServerAttachEntity detachEntity = new WrapperPlayServerAttachEntity();
        detachEntity.setEntityID(getEntityID());
        detachEntity.setVehicleId(-1);
        detachEntity.setLeash(false);

        final WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
        teleport.setEntityID(getEntityID());
        teleport.setX(getLocation().getX());
        teleport.setY(getLocation().getY());
        teleport.setZ(getLocation().getZ());

        final WrapperPlayServerEntityTeleport teleportVehicle = new WrapperPlayServerEntityTeleport();
        teleport.setEntityID(vehicleId);
        teleport.setX(getLocation().getX());
        teleport.setY(getLocation().getY()+0.48);
        teleport.setZ(getLocation().getZ());

        final WrapperPlayServerAttachEntity attachEntity = new WrapperPlayServerAttachEntity();
        attachEntity.setEntityID(getEntityID());
        attachEntity.setVehicleId(vehicleId);
        attachEntity.setLeash(false);
        return Arrays.asList(detachEntity.getHandle(), teleport.getHandle(), teleportVehicle.getHandle(), attachEntity.getHandle());
    }

    protected List<PacketContainer> createMetadataPackets() {
        final WrapperPlayServerEntityMetadata metadataVehicle = new WrapperPlayServerEntityMetadata();
        metadataVehicle.setEntityID(vehicleId);
        metadataVehicle.setMetadata(buildMetadataVehicle().getWatchableObjects());

        final WrapperPlayServerAttachEntity attachEntity = new WrapperPlayServerAttachEntity();
        attachEntity.setLeash(false);
        final PacketContainer attach = attachEntity.getHandle();
        attach.getIntegers().write(1, getEntityID());
        attach.getIntegers().write(2, vehicleId);

        final WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(getEntityID());
        metadata.setMetadata(buildMetadata().getWatchableObjects());
        return Arrays.asList(metadataVehicle.getHandle(), metadata.getHandle(), attach);
    }

    private WrappedDataWatcher buildMetadataVehicle() {
        final WrappedDataWatcher metadata = new WrappedDataWatcher();
        metadata.setObject(0, (byte) 0x20); // Set invisible
        return metadata;
    }

    private WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher metadata = new WrappedDataWatcher();
        metadata.setObject(1, (byte) 0x04); // Set riding
        metadata.setObject(10, itemStack); // Set itemstack
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
                "itemStack=" + itemStack +
                ", vehicleId=" + vehicleId +
                '}';
    }
}
