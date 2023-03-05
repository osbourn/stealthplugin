package me.osbourn.stealthplugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ProtectLayersHandler extends TogglableHandler {
    private int layer = 0;

    public ProtectLayersHandler() {
        super();
        this.setActive(false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isActive()) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isActive()) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.isActive()) {
            event.blockList().removeIf(block -> block.getLocation().getBlockY() <= this.getLayer());
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (this.isActive()) {
            if (event.getBlock().getLocation().getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    protected String description() {
        return "Protected layer";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return super.onCommand(sender, command, label, args);
        }

        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (args.length > 1) {
            sender.sendMessage("Too many arguments");
            return false;
        }

        try {
            int layer = Integer.parseInt(args[0]);
            this.setLayer(layer);
            if (this.isActive()) {
                sender.sendMessage("Protected layer set to " + layer);
            } else {
                sender.sendMessage("Protected layer set to " + layer + ", but protected layer is still disabled");
            }
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number");
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
