package de.exceptionflug.mccommons.scoreboards.localized;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Preconditions;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.scoreboards.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class LocalizedConfigBoard {

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
	private static final AbstractBoardHolder NULL_BOARD_HOLDER = new AbstractBoardHolder(new UUID(0, 0)) {
		@Override
		public void accept(PacketContainer container) {
		}
	};

	private final ConfigWrapper config;
	private final int id = ID_GENERATOR.incrementAndGet();
	private final BoardPacketAdapter packetAdapter = new BoardPacketAdapter();
	private final Scoreboard scoreboard = new Scoreboard();
	private final Map<String, Function<AbstractBoardHolder, String>> replacements = new HashMap<>();

	public LocalizedConfigBoard(final ConfigWrapper configWrapper) {
		config = configWrapper;
		for (final String objName : configWrapper.getKeys("Objectives")) {
			final Objective objective = scoreboard.registerNewObjective(objName, configWrapper.getOrSetDefault("Objectives." + objName + ".criteria", "dummy"));
			for (final String key : configWrapper.getKeys("Objectives." + objName + ".scores")) {
				final int score = configWrapper.getOrSetDefault("Objectives." + objName + ".scores." + key + ".score", 1);
				final Score scoreObj = objective.getScore("{!}" + id + "." + objName + "." + key);
				scoreObj.setScore(score);
			}
			objective.setDisplayName("{!}" + id + "." + objName);
			objective.setDisplaySlot(DisplaySlot.valueOf(configWrapper.getOrSetDefault("Objectives." + objName + ".displaySlot", "SIDEBAR")));
		}
		ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
	}

	public Score getScore(final Objective objective, final String messageKey, final String defaultMessage) {
		final Score scoreObj = objective.getScore("{!}" + id + "." + objective.getName() + ".custom." + messageKey);
		config.getLocalizedString(Providers.get(LocaleProvider.class).getFallbackLocale(), "Objectives." + objective.getName() + ".custom", "." + messageKey, defaultMessage);
		return scoreObj;
	}

	public void format(final String... replacements) {
		Preconditions.checkNotNull(replacements, "Replacements cannot be null");
		final Map<String, String> repMap = FormatUtils.createReplacementMap(replacements);
		for (final String key : repMap.keySet()) {
			if (key == null) {
				continue;
			}
			String val;
			if (repMap.get(key) == null) {
				val = "null";
			} else {
				val = repMap.get(key);
			}
			this.replacements.put(key, abstractBoardHolder -> val);
		}
		update();
	}

	public void format(final String key, Function<AbstractBoardHolder, String> replacer) {
		Preconditions.checkNotNull(key, "The key cannot be null.");
		replacements.put(key, replacer);
		update();
	}

	private List<Score> preUpdate() {
		final List<Score> resend = new ArrayList<>();
		for (final AbstractBoardHolder boardHolder : Scoreboards.getBoardHolders(scoreboard)) {
			for (final Objective objective : scoreboard.getObjectives()) {
				for (final Score score : objective.getScores()) {
					final String localizedString = getLocalizedString(boardHolder, score);
					for (final String placeholder : getUsedPlaceHolders(localizedString)) {
						final String apply = replacements.computeIfAbsent(placeholder, s -> abstractBoardHolder -> placeholder).apply(boardHolder);
						final String s = packetAdapter.getLastState().computeIfAbsent(boardHolder.getUniqueId(), uuid -> new HashMap<>()).get(placeholder);
						if (!Objects.equals(s, apply)) {
							boardHolder.accept(score.createRemovePacket());
							resend.add(score);
							break;
						}
					}
				}
			}
		}
		return resend;
	}

	private String getLocalizedString(final AbstractBoardHolder boardHolder, final Score score) {
		final String key = score.getEntry().substring(3);
		final String[] split = key.split("\\.");
		final String localizedString;
		if (split[2].equals("custom")) {
			localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(boardHolder.getUniqueId()), "Objectives." + split[1] + ".custom", "." + split[3], "&6Entry");
		} else {
			localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(boardHolder.getUniqueId()), "Objectives." + split[1] + ".scores." + split[2], ".entry", "&6Entry");
		}
		return localizedString;
	}

	public void update() {
		final List<Score> scores = preUpdate();
		for (final AbstractBoardHolder boardHolder : Scoreboards.getBoardHolders(scoreboard)) {
			for (final String k : this.replacements.keySet()) {
				packetAdapter.getLastState().computeIfAbsent(boardHolder.getUniqueId(), uuid -> new HashMap<>()).put(k, this.replacements.get(k).apply(boardHolder));
			}
		}
		for (final AbstractBoardHolder boardHolder : Scoreboards.getBoardHolders(scoreboard)) {
			for (final Objective objective : scoreboard.getObjectives()) {
				boardHolder.accept(objective.createRenamePacket());
			}
		}
		for (final AbstractBoardHolder boardHolder : Scoreboards.getBoardHolders(scoreboard)) {
			for (final Score score : scores) {
				boardHolder.accept(score.createSetScorePacket());
			}
		}
		for (final String k : this.replacements.keySet()) {
			packetAdapter.getAny().put(k, this.replacements.get(k).apply(NULL_BOARD_HOLDER));
		}
	}

	private List<String> getUsedPlaceHolders(final String in) {
		final List<String> out = new ArrayList<>();
		boolean percent = false;
		StringBuilder builder = new StringBuilder("%");
		for (final char c : in.toCharArray()) {
			if (c == '%') {
				if (percent) {
					builder.append("%");
					out.add(builder.toString());
					builder = new StringBuilder("%");
				}
				percent = !percent;
			} else if (percent) {
				builder.append(c);
			}
		}
		return out;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public ConfigWrapper getConfig() {
		return config;
	}

	public void destroy() {
		ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);
		for (final AbstractBoardHolder boardHolder : Scoreboards.getBoardHolders(scoreboard)) {
			Scoreboards.show(boardHolder, null);
		}
	}

	public int getId() {
		return id;
	}

	class BoardPacketAdapter extends PacketAdapter {

		private final Map<UUID, Map<String, String>> lastState = new ConcurrentHashMap<>();
		private final Map<String, String> any = new ConcurrentHashMap<>();

		BoardPacketAdapter() {
			super(Providers.get(JavaPlugin.class), PacketType.Play.Server.SCOREBOARD_SCORE, PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
		}

		@Override
		public void onPacketSending(final PacketEvent event) {
			if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_SCORE) {
				final WrapperPlayServerScoreboardScore scoreboardScore = new WrapperPlayServerScoreboardScore(event.getPacket().deepClone());
				if (scoreboardScore.getScoreName() != null && scoreboardScore.getScoreName().startsWith("{!}")) { // Ensure mccommons placeholder
					final String key = scoreboardScore.getScoreName().substring(3);
					final String[] split = key.split("\\.");
					if (Integer.parseInt(split[0]) != id)
						return;
					if (scoreboard.getObjective(split[1]) == null)
						return;
					final String localizedString;
					if (split[2].equals("custom")) {
						localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(event.getPlayer().getUniqueId()), "Objectives." + split[1] + ".custom", "." + split[3], "&6Entry");
					} else {
						localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(event.getPlayer().getUniqueId()), "Objectives." + split[1] + ".scores." + split[2], ".entry", "&6Entry");
					}
					scoreboardScore.setScoreName(FormatUtils.formatAmpersandColorCodes(FormatUtils.format(localizedString, lastState.computeIfAbsent(event.getPlayer().getUniqueId(), uuid -> any))));
					if (event.getPlayer().isOnline()) {
						scoreboardScore.sendPacket(event.getPlayer());
					}
					event.setCancelled(true);
				}
			} else {
				final WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective(event.getPacket());
				if (objective.getDisplayName() == null) {
					return;
				}
				String displayName = BaseComponent.toLegacyText(ComponentSerializer.parse(objective.getDisplayName().getJson()));
				if (displayName.startsWith("{!}")) { // Ensure mccommons placeholder
					if (Integer.parseInt(displayName.substring(3).split("\\.")[0]) != id)
						return;
					if (scoreboard.getObjective(objective.getName()) == null)
						return;
					final String localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(event.getPlayer().getUniqueId()), "Objectives." + objective.getName(), ".displayName", "&eObjective");
					objective.setDisplayName(WrappedChatComponent.fromLegacyText(FormatUtils.formatAmpersandColorCodes(FormatUtils.format(localizedString, lastState.computeIfAbsent(event.getPlayer().getUniqueId(), uuid -> any)))));
					if (event.getPlayer().isOnline()) {
						objective.sendPacket(event.getPlayer());
					}
					event.setCancelled(true);
				}
			}
		}

		public Map<UUID, Map<String, String>> getLastState() {
			return lastState;
		}

		public Map<String, String> getAny() {
			return any;
		}
	}

}
