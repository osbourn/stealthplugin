package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.newsettings.Settings;
import me.osbourn.stealthplugin.util.MaterialsUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SelectTargetsCommand implements CommandExecutor {
    private final GameManager gameManager;

    public SelectTargetsCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stealth.player")) {
            return false;
        }

        if (args.length != 1) {
            return false;
        }

        if (!this.gameManager.isPrepTime()) {
            sender.sendMessage(ChatColor.RED + "You can only choose objectives during prep time!");
            return true;
        }

        if (sender instanceof Player p && !this.gameManager.isOnDefenders(p)) {
            sender.sendMessage(ChatColor.RED + "Only defenders can select objectives!");
            return true;
        }

        Material material;
        try {
            material = Material.valueOf(args[0].toUpperCase());
        } catch(IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Unknown material " + args[0].toUpperCase());
            return true;
        }

        if (!this.gameManager.getGameTargets().getAvailableTargets().contains(material)) {
            sender.sendMessage(String.format("%s%s is not available as a target", ChatColor.RED,
                    MaterialsUtil.prettyMaterialName(material.toString())));
            return true;
        }

        if (this.gameManager.getGameTargets().getActiveTargets().contains(material)) {
            sender.sendMessage(String.format("%s%s has already been selected for this game", ChatColor.RED,
                    MaterialsUtil.prettyMaterialName(material.toString())));
            return true;
        }

        if (this.gameManager.getGameTargets().getActiveTargets().size() >= Settings.numberOfTargets) {
            sender.sendMessage(ChatColor.RED + "You have already selected all your objectives!");
            return true;
        }

        this.gameManager.getGameTargets().getActiveTargets().add(material);
        if (sender instanceof Player p) {
            broadcastSelection(p, MaterialsUtil.prettyMaterialName(material.toString()));
        }

        return true;
    }

    private void broadcastSelection(Player selector, String targetName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (selector.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(String.format("%sYou selected %s as a target",
                        ChatColor.GRAY, targetName));
            } else if (gameManager.isOnDefenders(player)) {
                player.sendMessage(String.format("%s%s has selected %s as a target",
                        ChatColor.GRAY, selector.getName(), targetName));
            }
        }
    }
}
