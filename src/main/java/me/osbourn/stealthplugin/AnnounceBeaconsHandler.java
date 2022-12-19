package me.osbourn.stealthplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

public class AnnounceBeaconsHandler implements Listener, CommandExecutor {
    private boolean isActive = true;

    private void announceDestruction(Block block, String adjective) {
        Block blockBelow = block.getRelative(0, -1, 0);
        String blockBelowName = switch (blockBelow.getType()) {
            case IRON_BLOCK -> "Iron Block";
            case GOLD_BLOCK -> "Gold Block";
            case DIAMOND_BLOCK -> "Diamond Block";
            case EMERALD_BLOCK -> "Emerald Block";
            case NETHERITE_BLOCK -> "Netherite Block";
            default -> blockBelow.getType().toString();
        };
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(String.format("Beacon above %s %s", blockBelowName, adjective));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isActive() && event.getBlock().getType() == Material.BEACON) {
            announceDestruction(event.getBlock(), "was broken");
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        if (this.isActive()) {
            for (Block block : event.blockList()) {
                if (block.getType() == Material.BEACON) {
                    announceDestruction(block, "was blown up");
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.toggleannouncebeacons")) {
            return false;
        }

        this.setActive(!this.isActive());
        if (this.isActive()) {
            sender.sendMessage("Beacon destruction announcements enabled");
        } else {
            sender.sendMessage("Beacon destruction announcements disabled");
        }
        return true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean state) {
        this.isActive = state;
    }
}