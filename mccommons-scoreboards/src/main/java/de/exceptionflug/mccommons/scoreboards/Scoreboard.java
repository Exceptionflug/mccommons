package de.exceptionflug.mccommons.scoreboards;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scoreboard {

	private final Set<Team> teams = new HashSet<>();
	final Map<DisplaySlot, Objective> displaySlotObjectiveMap = new HashMap<>();

	public Set<Team> getTeams() {
		return teams;
	}

	public Objective getObjectiveOfDisplaySlot(final DisplaySlot slot) {
		return displaySlotObjectiveMap.get(slot);
	}

	public DisplaySlot getDisplaySlotOfObjective(final Objective objective) {
		for (final DisplaySlot slot : displaySlotObjectiveMap.keySet()) {
			if (displaySlotObjectiveMap.get(slot).getName().equals(objective.getName()))
				return slot;
		}
		return null;
	}

	public Objective registerNewObjective(final String name, final String criteria) {
		final Objective objective = new Objective(this, name, criteria);
		displaySlotObjectiveMap.put(null, objective);
		broadcastPacket(createObjectiveCreatePacket(objective));
		return objective;
	}

	public Objective getObjective(final String name) {
		for (final DisplaySlot dsp : displaySlotObjectiveMap.keySet()) {
			if (displaySlotObjectiveMap.get(dsp).getName().equals(name)) return displaySlotObjectiveMap.get(dsp);
		}
		return null;
	}

	public Set<Objective> getObjectives() {
		return new HashSet<>(displaySlotObjectiveMap.values());
	}

	public void clearSlot(final DisplaySlot slot) {
		final Objective objective = getObjectiveOfDisplaySlot(slot);
		displaySlotObjectiveMap.remove(slot);
		displaySlotObjectiveMap.put(null, objective);
		final WrapperPlayServerScoreboardDisplayObjective displayObjective = new WrapperPlayServerScoreboardDisplayObjective();
		displayObjective.setScoreName("");
		if (slot == DisplaySlot.PLAYER_LIST)
			displayObjective.setPosition(0);
		else if (slot == DisplaySlot.SIDEBAR)
			displayObjective.setPosition(1);
		else
			displayObjective.setPosition(2);
		broadcastPacket(displayObjective.getHandle());
	}

	public Team registerNewTeam(final String name) {
		final Team team = new Team(this, name);
		teams.add(team);
		broadcastPacket(createTeamCreatePacket(team));
		return team;
	}

	public Team getTeam(final String teamName) {
		for (final Team team : teams) {
			if (team.getName().equals(teamName)) return team;
		}
		return null;
	}

	// RAW PACKETS

	public PacketContainer createObjectiveCreatePacket(final Objective objective) {
		final WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
		packet.setName(objective.getName());
		if (objective.getDisplayName() != null)
			packet.setDisplayName(WrappedChatComponent.fromLegacyText(objective.getDisplayName()));
		packet.setMode(0);
		packet.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
		return packet.getHandle();
	}

	public PacketContainer createTeamCreatePacket(final Team team) {
		final WrapperPlayServerScoreboardTeam scoreboardTeam = new WrapperPlayServerScoreboardTeam();
		scoreboardTeam.setName(team.getName());
		scoreboardTeam.setMode(0);
		scoreboardTeam.setDisplayName(WrappedChatComponent.fromLegacyText(team.getDisplayName()));
		scoreboardTeam.setPrefix(WrappedChatComponent.fromLegacyText(team.getPrefix()));
		scoreboardTeam.setSuffix(WrappedChatComponent.fromLegacyText(team.getSuffix()));
		scoreboardTeam.setPackOptionData(team.isFriendlyFire() ? 0x01 : 0x03);
		scoreboardTeam.setNameTagVisibility(team.getNameTagVisibility().serialize());
		scoreboardTeam.setColor(team.getColor());
		scoreboardTeam.setPlayers(team.getMembers());
		return scoreboardTeam.getHandle();
	}

	public void broadcastPacket(final PacketContainer container) {
		for (final AbstractBoardHolder holder : Scoreboards.getBoardHolders(this)) {
			holder.accept(container);
		}
	}

}
