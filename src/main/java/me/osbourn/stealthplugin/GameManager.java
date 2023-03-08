package me.osbourn.stealthplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager extends BukkitRunnable implements Listener {
    private final StealthPlugin plugin;
    private final Scoreboard scoreboard;
    /**
     * The scoreboard objective which is used to render game information.
     * This isn't an actual "objective" in the sense of it being a score, it is just used to draw information in the
     * sidebar.
     */
    private final Objective scoreboardObjective;
    private final ObjectiveDisplayHandler scoreboardObjectiveDisplayHandler;

    private int timeRemaining;
    private boolean isRoundActive;

    public GameManager(StealthPlugin plugin) {
        this.plugin = plugin;
        this.timeRemaining = 600;
        this.isRoundActive = false;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreboardObjective = this.scoreboard.registerNewObjective("stealthgame", "dummy",
                ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Game Info");
        this.scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboardObjectiveDisplayHandler = new ObjectiveDisplayHandler(this.scoreboardObjective);
    }

    public boolean isRoundActive() {
        return this.isRoundActive;
    }

    @Override
    public void run() {
        if (this.isRoundActive) {
            this.scoreboardObjectiveDisplayHandler.updateObjective(getScoreboardLines());

            updateGame();

            if (timeRemaining <= 0) {
                onTimeUp();
            } else {
                timeRemaining--;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(this.scoreboard);
    }

    private List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();

        lines.add("Players:");
        Map<Team, List<Player>> teams = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (!teams.containsKey(team)) {
                teams.put(team, new ArrayList<>());
            }
            teams.get(team).add(player);
        }
        for (Map.Entry<Team, List<Player>> entry : teams.entrySet()) {
            lines.add(entry.getKey().getName());
            for (Player player : entry.getValue()) {
                lines.add(player.getDisplayName());
            }
        }

        lines.add("" + this.timeRemaining);

        return lines;
    }

    private void onTimeUp() {
        announceMessage("Time's Up!");
        this.isRoundActive = false;
    }

    private void updateGame() {

    }

    /**
     * Start or reset the game
     */
    public void resetGame() {
        this.timeRemaining = 20;
        this.isRoundActive = true;
    }

    private void announceMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }
}
