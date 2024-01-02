package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.util.GameLoop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class GameCommand implements CommandExecutor {
    private final GameManager gameManager;
    private final JavaPlugin plugin;
    private final GameLoop gameLoop;

    public GameCommand(GameManager gameManager, JavaPlugin plugin, GameLoop gameLoop) {
        this.gameManager = gameManager;
        this.plugin = plugin;
        this.gameLoop = gameLoop;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Invalid number of arguments");
            return false;
        }

        switch (args[0]) {
            case "start" -> {
                if (this.gameManager.isTimerActive()) {
                    sender.sendMessage("Game is already active!");
                    return false;
                } else {
                    this.gameManager.resetGame();
                    return true;
                }
            }
            case "forcestart" -> {
                this.gameManager.resetGame();
                return true;
            }
            case "pasteandforcestart" -> {
                boolean pasteWasSuccess = PasteStructureCommand.pasteStructure(plugin, sender);
                if (!pasteWasSuccess) {
                    return false;
                }
                this.gameManager.resetGame();
                return true;
            }
            case "pausetimer" -> {
                if (this.gameManager.isTimerActive()) {
                    this.gameManager.setTimerActive(false);
                    return true;
                } else {
                    sender.sendMessage("Timer was already stopped!");
                    return false;
                }
            }
            case "resumetimer" -> {
                if (!this.gameManager.isTimerActive()) {
                    this.gameManager.setTimerActive(true);
                    return true;
                } else {
                    sender.sendMessage("Timer was already active!");
                    return false;
                }
            }
            case "tolobby" -> {
                sender.sendMessage("Sending players to lobby...");
                this.gameManager.sendPlayersToLobby();
                return true;
            }
            case "readynext" -> {
                sender.sendMessage("Sending players to lobby...");
                this.gameManager.sendPlayersToLobby();
                sender.sendMessage("Swapping roles...");
                SwapRolesCommand.swapRoles();
                sender.sendMessage("Pasting structure...");
                boolean pasteWasSuccess = PasteStructureCommand.pasteStructure(plugin, sender);
                if (pasteWasSuccess) {
                    sender.sendMessage("Structure pasted");
                } else {
                    sender.sendMessage("Paste was unsuccessful");
                }
                return true;
            }
            case "auto" -> {
                if (args.length < 2) {
                    return false;
                }
                switch (args[1]) {
                    case "on" -> {
                        this.gameLoop.setActive(true);
                        sender.sendMessage("Automatic game loop is now on");
                        return true;
                    }
                    case "off" -> {
                        this.gameLoop.setActive(false);
                        sender.sendMessage("Automatic game loop is now off");
                        return true;
                    }
                    case "cancel" -> {
                        boolean didCancel = this.gameLoop.cancelOnce();
                        if (!didCancel) {
                            sender.sendMessage("Game start was not currently scheduled");
                        }
                        return true;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            default -> {
                return false;
            }
        }
    }
}
