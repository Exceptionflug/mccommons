package de.exceptionflug.mccommons.holograms.line;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.holograms.Holograms;
import de.exceptionflug.mccommons.holograms.events.HologramLineShowEvent;
import de.exceptionflug.mccommons.holograms.util.EntityIDFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public abstract class AbstractHologramLine implements HologramLine {

    private final Set<UUID> viewers = new ConcurrentSkipListSet<>();
    private final int entityId;
    private Location location;

    public AbstractHologramLine() {
        this.entityId = EntityIDFactory.getAndIncrement();
    }

    public AbstractHologramLine(final Location location) {
        this.entityId = EntityIDFactory.getAndIncrement();
        this.location = location;
    }

    @Override
    public int getEntityID() {
        return entityId;
    }

    @Override
    public void despawn() {
        this.location = null;
        final List<PacketContainer> despawnPackets = createDespawnPackets();
        viewers.forEach(it -> distribute(despawnPackets, it));
        viewers.clear();
    }

    @Override
    public void despawnFor(final Player player) {
        Preconditions.checkNotNull(player, "The player cannot be null!");
        distribute(createDespawnPackets(), player.getUniqueId());
        viewers.remove(player.getUniqueId());
    }

    @Override
    public void spawn(final Location location) {
        Preconditions.checkNotNull(location, "The location cannot be null!");
        Preconditions.checkNotNull(location.getWorld(), "The location.getWorld() cannot be null!");
        this.location = location;
        final List<PacketContainer> spawnPackets = createSpawnPackets();
        location.getWorld().getPlayers().forEach(it -> {
            if(it.getLocation().distance(location) > Holograms.hologramTrackingDistance)
                return;
            final HologramLineShowEvent event = new HologramLineShowEvent(it, this);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled())
                return;
            distribute(spawnPackets, it.getUniqueId());
            viewers.add(it.getUniqueId());
        });
    }

    @Override
    public void spawnFor(final Player player) {
        Preconditions.checkNotNull(location, "The location cannot be null!");
        Preconditions.checkNotNull(player, "The player cannot be null!");
        if(player.getLocation().distance(location) > Holograms.hologramTrackingDistance+5) {
            return;
        }
        final HologramLineShowEvent event = new HologramLineShowEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
        distribute(createSpawnPackets(), player.getUniqueId());
        viewers.add(player.getUniqueId());
    }

    @Override
    public void spawnFor(final Location location, final Player player) {
        Preconditions.checkNotNull(location, "The location cannot be null!");
        Preconditions.checkNotNull(player, "The player cannot be null!");
        this.location = location;
        spawnFor(player);
    }

    @Override
    public void update() {
        final List<PacketContainer> metadataPackets = createMetadataPackets();
        viewers.forEach(it -> distribute(metadataPackets, it));
    }

    @Override
    public void teleport(final Location location) {
        Preconditions.checkNotNull(location, "The location cannot be null!");
        this.location = location;
        viewers.forEach(it -> distribute(createTeleportPackets(), it));
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public Set<UUID> getViewers() {
        return viewers;
    }

    private void distribute(final List<PacketContainer> packetContainers, final UUID uuid) {
        final Player p = Bukkit.getPlayer(uuid);
        if(p == null)
            return;
        for(final PacketContainer packetContainer : packetContainers) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, packetContainer);
            } catch (final InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract List<PacketContainer> createSpawnPackets();
    protected abstract List<PacketContainer> createTeleportPackets();
    protected abstract List<PacketContainer> createDespawnPackets();
    protected abstract List<PacketContainer> createMetadataPackets();

}
