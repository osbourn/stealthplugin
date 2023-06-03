package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GameCommand implements CommandExecutor {
    private final GameManager gameManager;
    private final PasteStructureCommand structurePaster;

    public GameCommand(GameManager gameManager, PasteStructureCommand structurePaster) {
        this.gameManager = gameManager;
        this.structurePaster = structurePaster;
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
                boolean pasteWasSuccess = this.structurePaster.pasteStructure(sender);
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
            default -> {
                return false;
            }
        }
    }
}
