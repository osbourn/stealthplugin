package me.osbourn.stealthplugin.util;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.commands.PasteStructureCommand;
import me.osbourn.stealthplugin.commands.SwapRolesCommand;
import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

/**
 * Responsible for setting up and starting the next round automatically
 */
public class GameLoop {
    private final JavaPlugin plugin;
    private final GameManager gameManager;
    private boolean active = false;
    private @Nullable BukkitRunnable currentRunnable = null;

    public GameLoop(JavaPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        if (!active) {
            this.cancelOnce();
        }
        this.active = active;
    }

    /**
     * Prevents the game from starting automatically one time.
     *
     * @return true if the start was cancelled, false if the game was not set to start automatically soon.
     */
    public boolean cancelOnce() {
        if (active && this.currentRunnable != null) {
            this.currentRunnable.cancel();
            this.currentRunnable = null;
            AnnouncementUtils.announce(ChatColor.RED + "Game start cancelled");
            return true;
        }
        return false;
    }

    /**
     * Sets up a runnable to send players to the lobby and start the next game. As an implementation detail, a new
     * runnable is made every time this is called. If a start is already scheduled, cancel it and reschedule it.
     */
    public void runRunnable() {
        if (this.currentRunnable != null) {
            this.cancelOnce();
        }
        this.currentRunnable = new GameLoopRunnable(gameManager, plugin, () -> this.currentRunnable = null);
        this.currentRunnable.runTaskTimer(this.plugin, 20, 20);
    }

    public void runRunnableIfActive() {
        if (this.isActive()) {
            this.runRunnable();
        }
    }

    /**
     * The different actions to be run before starting the next game, mainly used by GameLoopRunnable to
     * keep track of what it should do next.
     */
    private enum LoopAction {
        SEND_PLAYERS_TO_LOBBY,
        START_GAME;

        public String actionText() {
            return switch (this) {
                case SEND_PLAYERS_TO_LOBBY -> "Sending players to lobby";
                case START_GAME -> "Starting game";
            };
        }
    }

    private static class GameLoopRunnable extends BukkitRunnable {
        private LoopAction nextAction = LoopAction.SEND_PLAYERS_TO_LOBBY;
        private int timeUntilNextAction = 10;
        private final GameManager gameManager;
        private final JavaPlugin plugin;
        private final Runnable afterFinishCode;

        /**
         * Creates a BukkitRunnable responsible for ending the game and starting the next game.
         *
         * @param gameManager The game manager to affect
         * @param afterFinishCode The code that should be run after this runnable finishes all its actions.
         */
        public GameLoopRunnable(GameManager gameManager, JavaPlugin plugin, Runnable afterFinishCode) {
            this.gameManager = gameManager;
            this.plugin = plugin;
            this.afterFinishCode = afterFinishCode;
        }

        @Override
        public void run() {
            this.timeUntilNextAction--;
            if (this.timeUntilNextAction <= 0) {
                switch (this.nextAction) {
                    case SEND_PLAYERS_TO_LOBBY -> {
                        this.sendPlayersToLobby();
                        this.nextAction = LoopAction.START_GAME;
                        this.timeUntilNextAction = Settings.timeInLobby;
                    }
                    case START_GAME -> {
                        this.startGame();
                        this.cancel();
                        this.afterFinishCode.run();
                    }
                }
                AnnouncementUtils.announce(String.format("%s%s in %d seconds",
                        ChatColor.GREEN, this.nextAction.actionText(), this.timeUntilNextAction));
            } else if (this.timeUntilNextAction <= 5 || this.timeUntilNextAction == 10) {
                AnnouncementUtils.announce(String.format("%s%s in %d seconds",
                        ChatColor.GREEN, this.nextAction.actionText(), this.timeUntilNextAction));
            }
        }

        private void sendPlayersToLobby() {
            this.gameManager.sendPlayersToLobby();
            SwapRolesCommand.swapRoles();
            boolean pasteStructureResult = PasteStructureCommand.pasteStructure(this.plugin, null);
            if (!pasteStructureResult) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("stealth.manage")) {
                        player.sendMessage(ChatColor.RED + "Structure failed to paste");
                    }
                }
            }
        }

        private void startGame() {
            this.gameManager.resetGame();
        }
    }
}
