package de.exceptionflug.mccommons.scoreboards;

import de.exceptionflug.mccommons.core.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;
import de.exceptionflug.mccommons.core.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Objective {

	private final Scoreboard scoreboard;
	private final Set<Score> scores = new HashSet<>();
	private String displayName, name;
	private String criteria;

	public Objective(final Scoreboard scoreboard, final String name, final String criteria) {
		this.scoreboard = scoreboard;
		this.name = this.displayName = name;
		this.criteria = criteria;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getCriteria() {
		return criteria;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public DisplaySlot getDisplaySlot() {
		return getScoreboard().getDisplaySlotOfObjective(this);
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
		broadcastPacket(createRenamePacket());
	}

	public void setDisplaySlot(final DisplaySlot slot) {
		final DisplaySlot oldSlot = getDisplaySlot();
		scoreboard.displaySlotObjectiveMap.remove(oldSlot);
		scoreboard.displaySlotObjectiveMap.put(slot, this);
		broadcastPacket(createPositionPacket());
	}

	public void unregister() {
		getScoreboard().displaySlotObjectiveMap.remove(getDisplaySlot());
		broadcastPacket(createDestroyPacket());
	}

	public Set<Score> getScores() {
		return scores;
	}

	public Score getScore(final OfflinePlayer player) {
		for (final Score score : scores) {
			if (score.getPlayer() != null && score.getPlayer().equals(player))
				return score;
		}
		final Score score = new Score(this, player);
		scores.add(score);
		return score;
	}

	public Score getScore(final String entry) {
		for (final Score score : scores) {
			if (score.getEntry() != null && score.getEntry().equals(entry))
				return score;
		}
		final Score score = new Score(this, entry);
		scores.add(score);
		return score;
	}

	// RAW PACKETS

	public PacketContainer createRenamePacket() {
		final WrapperPlayServerScoreboardObjective playServerScoreboardObjective = new WrapperPlayServerScoreboardObjective();
		playServerScoreboardObjective.setName(name);
		playServerScoreboardObjective.setDisplayName(WrappedChatComponent.fromLegacyText(displayName));
		playServerScoreboardObjective.setMode(2);
		playServerScoreboardObjective.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
		return playServerScoreboardObjective.getHandle();
	}

	public PacketContainer createDestroyPacket() {
		final WrapperPlayServerScoreboardObjective playServerScoreboardObjective = new WrapperPlayServerScoreboardObjective();
		playServerScoreboardObjective.setName(name);
		playServerScoreboardObjective.setMode(1);
		return playServerScoreboardObjective.getHandle();
	}

	public PacketContainer createPositionPacket() {
		final WrapperPlayServerScoreboardDisplayObjective packet = new WrapperPlayServerScoreboardDisplayObjective();
		final DisplaySlot slot = getDisplaySlot();
		packet.setScoreName(name);
		if (slot == DisplaySlot.PLAYER_LIST)
			packet.setPosition(0);
		else if (slot == DisplaySlot.SIDEBAR)
			packet.setPosition(1);
		else if (slot == DisplaySlot.BELOW_NAME)
			packet.setPosition(2);
		return packet.getHandle();
	}

	public void broadcastPacket(final PacketContainer container) {
		for (final AbstractBoardHolder holder : Scoreboards.getBoardHolders(scoreboard)) {
			holder.accept(container);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Objective objective = (Objective) o;
		return scoreboard.equals(objective.scoreboard) &&
			scores.equals(objective.scores) &&
			Objects.equals(displayName, objective.displayName) &&
			name.equals(objective.name) &&
			criteria.equals(objective.criteria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scoreboard, scores, displayName, name, criteria);
	}
}
