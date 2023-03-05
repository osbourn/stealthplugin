package me.osbourn.stealthplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GameCommand implements CommandExecutor {
    private final GameManager gameManager;

    public GameCommand(GameManager gameManager) {
        this.gameManager = gameManager;
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

        if (args[0].equals("start")) {
            if (this.gameManager.isRoundActive()) {
                sender.sendMessage("Game is already active!");
                return false;
            } else {
                this.gameManager.resetGame();
                return true;
            }
        }

        return false;
    }
}
