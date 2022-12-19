package me.osbourn.stealthplugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ProtectLayersHandler implements Listener, CommandExecutor {
    private int layer = -1;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.getLayer() >= 0) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.getLayer() >= 0) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.getLayer() >= 0) {
            event.blockList().removeIf(block -> block.getLocation().getBlockY() <= this.getLayer());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.setprotectedlayer")) {
            return false;
        }

        if (args.length == 0) {
            this.setLayer(-1);
            sender.sendMessage("Protected layer disabled");
            return true;
        } else if (args.length == 1) {
            try {
                int layer = Integer.parseInt(args[0]);
                this.setLayer(layer);
                sender.sendMessage("Protected layer set to " + layer);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number");
                return false;
            }
        } else {
            sender.sendMessage("Too many arguments");
            return false;
        }
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
