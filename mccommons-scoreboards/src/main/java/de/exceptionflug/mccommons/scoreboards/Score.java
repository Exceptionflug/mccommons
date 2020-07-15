package de.exceptionflug.mccommons.scoreboards;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.OfflinePlayer;

public class Score {

	private final Objective objective;
	private OfflinePlayer player;
	private String entry;
	private int score;
	private boolean scoreSet;

	public Score(final Objective objective, final String entry) {
		this.objective = objective;
		this.entry = entry;
	}

	public Score(final Objective objective, final OfflinePlayer player) {
		this.objective = objective;
		this.player = player;
	}

	public void setScore(final int score) {
		this.score = score;
		scoreSet = true;
		broadcastPacket(createSetScorePacket());
	}

	public boolean isScoreSet() {
		return scoreSet;
	}

	public void setEntry(final String entry) {
		final boolean flag = isScoreSet();
		reset();
		this.entry = entry;
		if (flag)
			setScore(score);
	}

	public void setPlayer(final OfflinePlayer player) {
		final boolean flag = isScoreSet();
		reset();
		this.player = player;
		if (flag)
			setScore(score);
	}

	public Objective getObjective() {
		return objective;
	}

	public int getScore() {
		return score;
	}

	public String getEntry() {
		return entry;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void reset() {
		if (!scoreSet) return;
		scoreSet = false;
		broadcastPacket(createRemovePacket());
	}

	// RAW PACKETS

	public PacketContainer createSetScorePacket() {
		final WrapperPlayServerScoreboardScore scoreboardScore = new WrapperPlayServerScoreboardScore();
		scoreboardScore.setObjectiveName(objective.getName());
		scoreboardScore.setScoreboardAction(EnumWrappers.ScoreboardAction.CHANGE);
		scoreboardScore.setScoreName(player != null ? player.getName() : entry);
		scoreboardScore.setValue(score);
		return scoreboardScore.getHandle();
	}

	public PacketContainer createRemovePacket() {
		final WrapperPlayServerScoreboardScore scoreboardScore = new WrapperPlayServerScoreboardScore();
		scoreboardScore.setObjectiveName(objective.getName());
		scoreboardScore.setScoreboardAction(EnumWrappers.ScoreboardAction.REMOVE);
		scoreboardScore.setScoreName(player != null ? player.getName() : entry);
		return scoreboardScore.getHandle();
	}

	public void broadcastPacket(final PacketContainer container) {
		for (final AbstractBoardHolder holder : Scoreboards.getBoardHolders(objective.getScoreboard())) {
			holder.accept(container);
		}
	}

}
