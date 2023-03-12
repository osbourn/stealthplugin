package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.util.ObjectiveDisplayHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
    private final MorphManager morphManager;
    private final Scoreboard scoreboard;
    /**
     * The scoreboard objective which is used to render game information.
     * This isn't an actual "objective" in the sense of it being a score, it is just used to draw information in the
     * sidebar.
     */
    private final Objective scoreboardObjective;
    private final ObjectiveDisplayHandler scoreboardObjectiveDisplayHandler;
    private final IntegerSetting timePerRoundSetting;

    private int timeRemaining;
    private boolean isRoundActive;

    public GameManager(StealthPlugin plugin, MorphManager morphManager, IntegerSetting timePerRoundSetting) {
        this.plugin = plugin;
        this.timeRemaining = 600;
        this.isRoundActive = false;
        this.morphManager = morphManager;
        this.timePerRoundSetting = timePerRoundSetting;
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

        Map<Team, List<Player>> teams = new HashMap<>();
        // TODO: Consider rendering players not on teams
        List<Player> playersWithoutTeams = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (team == null) {
                playersWithoutTeams.add(player);
            } else {
                if (!teams.containsKey(team)) {
                    teams.put(team, new ArrayList<>());
                }
                teams.get(team).add(player);
            }
        }
        for (Map.Entry<Team, List<Player>> entry : teams.entrySet()) {
            ChatColor color = entry.getKey().getColor();
            lines.add(ChatColor.BOLD + entry.getKey().getName() + ":");
            for (Player player : entry.getValue()) {
                if (this.isPlayerEliminated(player)) {
                    lines.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + player.getName());
                } else {
                    lines.add(color + player.getName());
                }
            }
        }

        int minutesLeft = timeRemaining / 60;
        int secondsLeft = timeRemaining % 60;
        lines.add(String.format("%sTime: %02d:%02d", ChatColor.YELLOW, minutesLeft, secondsLeft));

        return lines;
    }

    private boolean isPlayerEliminated(Player player) {
        return player.getGameMode() == GameMode.SPECTATOR || this.morphManager.isPlayerMorphed(player) || player.isDead();
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
        this.timeRemaining = this.timePerRoundSetting.getValue();
        this.isRoundActive = true;
    }

    private void announceMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }
}
