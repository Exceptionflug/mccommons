package de.exceptionflug.mccommons.scoreboards;

import com.comphenix.protocol.events.PacketContainer;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractBoardHolder {

	private final UUID uuid;

	public AbstractBoardHolder(final UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public abstract void accept(final PacketContainer container);

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AbstractBoardHolder that = (AbstractBoardHolder) o;
		return uuid.equals(that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}
}
