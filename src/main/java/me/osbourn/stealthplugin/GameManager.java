package me.osbourn.stealthplugin;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager extends BukkitRunnable {
    private final StealthPlugin plugin;

    private int timeRemaining;
    private boolean isRoundActive;

    public GameManager(StealthPlugin plugin) {
        this.plugin = plugin;
        this.timeRemaining = 600;
        this.isRoundActive = false;
    }

    public boolean isRoundActive() {
        return this.isRoundActive;
    }

    @Override
    public void run() {
        if (this.isRoundActive) {
            // TODO: Remove test code
            announceMessage("" + timeRemaining);

            updateGame();

            if (timeRemaining <= 0) {
                onTimeUp();
            } else {
                timeRemaining--;
            }
        }
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
