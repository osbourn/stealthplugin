package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReviveCommand implements CommandExecutor {
    private final GameManager gameManager;

    public ReviveCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (args.length != 1) {
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("Unknown player " + args[0]);
            return false;
        }

        gameManager.readyPlayer(player);
        GiveTeamArmorCommand.giveTeamArmor();
        sender.sendMessage("Reset player " + args[0]);
        return true;
    }
}
