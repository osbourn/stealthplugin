package me.osbourn.stealthplugin;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreManager {
    private final Map<Team, Integer> scores;

    public ScoreManager() {
        scores = new HashMap<>();
    }

    public boolean isTeamRecorded(Team team) {
        return this.scores.containsKey(team);
    }

    public int getScore(Team team) {
        if (!this.isTeamRecorded(team)) {
            return 0;
        } else {
            return this.scores.get(team);
        }
    }

    public void setScore(Team team, int score) {
        this.scores.put(team, score);
    }

    public void incrementScore(Team team) {
        this.setScore(team, this.getScore(team) + 1);
    }

    public void unregisterTeam(Team team) {
        this.scores.remove(team);
    }

    public void resetAllScores() {
        for (Team team : this.scores.keySet()) {
            this.setScore(team, 0);
        }
    }

    public String getScoreDisplay() {
        var entries = new ArrayList<>(this.scores.entrySet());
        entries.sort(Comparator.comparing(e -> e.getKey().getName()));
        return entries.stream()
                .map(e -> e.getKey().getColor().toString() + ChatColor.BOLD + e.getValue() + ChatColor.RESET)
                .collect(Collectors.joining(" - "));
    }
}
