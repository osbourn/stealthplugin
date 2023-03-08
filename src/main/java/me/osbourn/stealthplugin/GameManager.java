package me.osbourn.stealthplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

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
                ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Stealth Game");
        this.scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboardObjectiveDisplayHandler = new ObjectiveDisplayHandler(this.scoreboardObjective);
    }

    public boolean isRoundActive() {
        return this.isRoundActive;
    }

    @Override
    public void run() {
        if (this.isRoundActive) {
            // TODO: Remove test code
            announceMessage("" + timeRemaining);
            this.scoreboardObjectiveDisplayHandler.updateObjective(List.of("" + timeRemaining));

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
