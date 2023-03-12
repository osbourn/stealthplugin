package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleGameScoreboardCommand implements CommandExecutor {
    private final GameManager gameManager;
    public ToggleGameScoreboardCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stealth.player")) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            player.setScoreboard(gameManager.getScoreboard());
        } else {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }

        return true;
    }
}
