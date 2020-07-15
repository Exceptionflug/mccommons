package de.exceptionflug.mccommons.scoreboards;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.events.PacketContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

	private final Scoreboard scoreboard;
	private final List<String> members = new ArrayList<>();
	private final String name;
	private String displayName, suffix = "", prefix = "";
	private boolean friendlyFire = true;
	private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;
	private int color = -1;

	public Team(final Scoreboard scoreboard, final String name) {
		this.scoreboard = scoreboard;
		this.name = name;
		displayName = name;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
		broadcastPacket(createUpdatePacket());
	}

	public void setColor(final int color) {
		this.color = color;
		broadcastPacket(createUpdatePacket());
	}

	public void setFriendlyFire(final boolean friendlyFire) {
		this.friendlyFire = friendlyFire;
		broadcastPacket(createUpdatePacket());
	}

	public void setNameTagVisibility(final NameTagVisibility nameTagVisibility) {
		this.nameTagVisibility = nameTagVisibility;
		broadcastPacket(createUpdatePacket());
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
		broadcastPacket(createUpdatePacket());
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
		broadcastPacket(createUpdatePacket());
	}

	public void addMember(final String member) {
		members.add(member);
		broadcastPacket(createAddMemberPacket(member));
	}

	public void removeMember(final String member) {
		members.remove(member);
		broadcastPacket(createRemoveMemberPacket(member));
	}

	public void unregister() {
		scoreboard.getTeams().remove(this);
		broadcastPacket(createDestroyPacket());
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public List<String> getMembers() {
		return members;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public boolean isFriendlyFire() {
		return friendlyFire;
	}

	public NameTagVisibility getNameTagVisibility() {
		return nameTagVisibility;
	}

	public int getColor() {
		return color;
	}

// RAW PACKETS

	public PacketContainer createUpdatePacket() {
		final WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		team.setName(name);
		team.setMode(2);
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.setDisplayName(displayName);
		team.setPackOptionData(friendlyFire ? 0x01 : 0x03);
		team.setNameTagVisibility(nameTagVisibility.serialized);
		team.setColor(color);
		return team.getHandle();
	}

	public PacketContainer createAddMemberPacket(final String name) {
		final WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		team.setName(this.name);
		team.setMode(3);
		team.setPlayers(Collections.singletonList(name));
		return team.getHandle();
	}

	public PacketContainer createRemoveMemberPacket(final String name) {
		final WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		team.setName(this.name);
		team.setMode(4);
		team.setPlayers(Collections.singletonList(name));
		return team.getHandle();
	}

	public PacketContainer createDestroyPacket() {
		final WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		team.setName(name);
		team.setMode(1);
		return team.getHandle();
	}

	public void broadcastPacket(final PacketContainer container) {
		for (final AbstractBoardHolder holder : Scoreboards.getBoardHolders(scoreboard)) {
			holder.accept(container);
		}
	}

	public enum NameTagVisibility {

		ALWAYS("always"), HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"), HIDE_FOR_OWN_TEAM("hideForOwnTeam"), NEVER("never");

		private final String serialized;

		NameTagVisibility(String ser) {
			serialized = ser;
		}

		public String serialize() {
			return serialized;
		}
	}

}
