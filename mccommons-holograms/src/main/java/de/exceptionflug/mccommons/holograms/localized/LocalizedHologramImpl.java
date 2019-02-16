package de.exceptionflug.mccommons.holograms.localized;

import com.comphenix.protocol.ProtocolLibrary;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.holograms.HologramImpl;
import de.exceptionflug.mccommons.holograms.line.HologramLine;
import de.exceptionflug.mccommons.holograms.line.ItemHologramLine;
import de.exceptionflug.mccommons.holograms.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class LocalizedHologramImpl extends HologramImpl implements LocalizedHologram {

    private final PacketListener packetListener;
    private final HologramImpl hologram;

    public LocalizedHologramImpl(final HologramImpl hologram, final PacketListener packetListener) {
        super(hologram.getLocation());
        this.hologram = hologram;
        this.packetListener = packetListener;
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
    }

    @Override
    public void update() {
        hologram.update();
    }

    @Override
    public void respawn() {
        hologram.respawn();
    }

    @Override
    public void despawn() {
        hologram.despawn();
        ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
    }

    @Override
    public void despawnFor(final Player player) {
        hologram.despawnFor(player);
    }

    @Override
    public void spawn() {
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
        hologram.spawn();
    }

    @Override
    public void spawnFor(final Player player) {
        hologram.spawnFor(player);
    }

    @Override
    public TextHologramLine appendLine(final String text) {
        return hologram.appendLine(text);
    }

    @Override
    public ItemHologramLine appendLine(final ItemStack itemStack) {
        return hologram.appendLine(itemStack);
    }

    @Override
    public void deleteLine(final int index) {
        hologram.deleteLine(index);
    }

    @Override
    public void deleteLine(final HologramLine line) {
        hologram.deleteLine(line);
    }

    @Override
    public TextHologramLine insertLine(final int index, final String text) {
        return hologram.insertLine(index, text);
    }

    @Override
    public ItemHologramLine insertLine(final int index, final ItemStack stack) {
        return hologram.insertLine(index, stack);
    }


    @Override
    public void teleport(final Location location) {
        hologram.teleport(location);
    }

    @Override
    public void setVgap(final double vgap) {
        hologram.setVgap(vgap);
    }

    @Override
    public double getHeight() {
        return hologram.getHeight();
    }

    @Override
    public double getVgap() {
        return hologram.getVgap();
    }

    @Override
    public int getID() {
        return hologram.getID();
    }

    @Override
    public boolean isDespawned() {
        return hologram.isDespawned();
    }

    @Override
    public List<HologramLine> getLines() {
        return hologram.getLines();
    }

    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }

    @Override
    public ConfigWrapper getMessageConfig() {
        return packetListener.getConfig();
    }

    @Override
    public void appendLine(final String msgKey, final String defaultMessage, final PlayerboundReplacementSupplier replacementSupplier) {
        packetListener.getHandleLines().add(new LineData(msgKey, defaultMessage, replacementSupplier));
        appendLine("{!}"+msgKey);
    }

    @Override
    public void appendLine(final String msgKey, final String defaultMessage, final String... nonDynamicReplacements) {
        packetListener.getHandleLines().add(new LineData(msgKey, defaultMessage, (p) -> nonDynamicReplacements));
        appendLine("{!}"+msgKey);
    }

    @Override
    public void appendLine(final String msgKey, final String defaultMessage) {
        packetListener.getHandleLines().add(new LineData(msgKey, defaultMessage, (p) -> new String[0]));
        appendLine("{!}"+msgKey);
    }
}
