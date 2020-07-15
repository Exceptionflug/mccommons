package de.exceptionflug.mccommons.scoreboards;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerBoardHolder extends AbstractBoardHolder {

	public PlayerBoardHolder(final UUID uuid) {
		super(uuid);
	}

	@Override
	public void accept(final PacketContainer container) {
		final Player player = Bukkit.getPlayer(getUniqueId());
		if (player == null) return;
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
