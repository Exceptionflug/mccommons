package de.exceptionflug.mccommons.scoreboards.localized;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.exceptionflug.mccommons.config.shared.ConfigWrapper;
import de.exceptionflug.mccommons.core.Providers;
import de.exceptionflug.mccommons.core.providers.LocaleProvider;
import de.exceptionflug.mccommons.core.utils.FormatUtils;
import de.exceptionflug.mccommons.scoreboards.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalizedConfigBoard {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private final ConfigWrapper config;
    private final int id = ID_GENERATOR.incrementAndGet();
    private final BoardPacketAdapter packetAdapter = new BoardPacketAdapter();
    private final Scoreboard scoreboard = new Scoreboard();
    private final Map<String, String> replacements = new HashMap<>();

    public LocalizedConfigBoard(final ConfigWrapper configWrapper) {
        config = configWrapper;
        for(final String objName : configWrapper.getKeys("Objectives")) {
            final Objective objective = scoreboard.registerNewObjective(objName, configWrapper.getOrSetDefault("Objectives." + objName + ".criteria", "dummy"));
            for(final String key : configWrapper.getKeys("Objectives."+objName+".scores")) {
                final int score = configWrapper.getOrSetDefault("Objectives."+objName+".scores."+key+".score", 1);
                final Score scoreObj = objective.getScore("{!}"+id+"."+objName+"."+key);
                scoreObj.setScore(score);
            }
            objective.setDisplayName("{!}"+id+"."+objName);
            objective.setDisplaySlot(DisplaySlot.valueOf(configWrapper.getOrSetDefault("Objectives."+objName+".displaySlot", "SIDEBAR")));
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
    }

    public Score getScore(final Objective objective, final String messageKey, final String defaultMessage) {
        final Score scoreObj = objective.getScore("{!}"+id+"."+objective.getName()+".custom."+messageKey);
        config.getLocalizedString(Providers.get(LocaleProvider.class).getFallbackLocale(), "Objectives."+objective.getName()+".custom", "."+messageKey, defaultMessage);
        return scoreObj;
    }

    public void format(final String... replacements) {
        final Map<String, String> repMap = FormatUtils.createReplacementMap(replacements);
        for(final Objective objective : scoreboard.getObjectives()) {
            for(final Score score : objective.getScores()) {
                score.reset();
            }
        }
        this.replacements.clear();
        this.replacements.putAll(repMap);
        for(final Objective objective : scoreboard.getObjectives()) {
            scoreboard.broadcastPacket(objective.createRenamePacket());
            for(final Score score : objective.getScores()) {
                score.setScore(score.getScore());
            }
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ConfigWrapper getConfig() {
        return config;
    }

    public void destroy() {
        ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);
        for(final AbstractBoardHolder boardHolder : Scoreboards.getBoardHolders(scoreboard)) {
            Scoreboards.show(boardHolder, null);
        }
    }

    public int getId() {
        return id;
    }

    class BoardPacketAdapter extends PacketAdapter {

        BoardPacketAdapter() {
            super(Providers.get(JavaPlugin.class), PacketType.Play.Server.SCOREBOARD_SCORE, PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        }

        @Override
        public void onPacketSending(final PacketEvent event) {
            if(event.getPacketType() == PacketType.Play.Server.SCOREBOARD_SCORE) {
                final WrapperPlayServerScoreboardScore scoreboardScore = new WrapperPlayServerScoreboardScore(event.getPacket().deepClone());
                if(scoreboardScore.getScoreName() != null && scoreboardScore.getScoreName().startsWith("{!}")) { // Ensure mccommons placeholder
                    final String key = scoreboardScore.getScoreName().substring(3);
                    final String[] split = key.split("\\.");
                    if(Integer.parseInt(split[0]) != id)
                        return;
                    if(scoreboard.getObjective(split[1]) == null)
                        return;
                    final String localizedString;
                    if(split[2].equals("custom")) {
                        localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(event.getPlayer().getUniqueId()), "Objectives." + split[1] + ".custom", "."+split[3], "&6Entry");
                    } else {
                        localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(event.getPlayer().getUniqueId()), "Objectives." + split[1] + ".scores." + split[2], ".entry", "&6Entry");
                    }
                    scoreboardScore.setScoreName(FormatUtils.formatAmpersandColorCodes(FormatUtils.format(localizedString, replacements)));
                    scoreboardScore.sendPacket(event.getPlayer());
                    event.setCancelled(true);
                }
            } else {
                final WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective(event.getPacket());
                if(objective.getDisplayName() != null && objective.getDisplayName().startsWith("{!}")) { // Ensure mccommons placeholder
                    if(Integer.parseInt(objective.getDisplayName().substring(3).split("\\.")[0]) != id)
                        return;
                    if(scoreboard.getObjective(objective.getName()) == null)
                        return;
                    final String localizedString = config.getLocalizedString(Providers.get(LocaleProvider.class).provide(event.getPlayer().getUniqueId()), "Objectives." + objective.getName(), ".displayName", "&eObjective");
                    objective.setDisplayName(FormatUtils.formatAmpersandColorCodes(FormatUtils.format(localizedString, replacements)));
                    objective.sendPacket(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

}
