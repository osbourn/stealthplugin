package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settingsapi.Setting;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.Optional;

public class ProtectLayersHandler implements Setting, Listener {
    private int layer = 0;
    private boolean isActive = false;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isActive) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isActive) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.isActive) {
            event.blockList().removeIf(block -> block.getLocation().getBlockY() <= this.getLayer());
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (this.isActive) {
            if (event.getBlock().getLocation().getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public String getName() {
        return "protectedlayer";
    }

    @Override
    public String getInfo() {
        if (this.isActive) {
            return "protectedlayer is enabled and set to " + this.layer;
        } else {

            return "protectedlayer is disabled, but is set to " + this.layer;
        }
    }

    @Override
    public Optional<String> trySet(String[] args) {
        if (args.length != 1) {
            return Optional.of("Incorrect number of arguments");
        }

        if (args[0].equals("disable")) {
            this.isActive = false;
            return Optional.empty();
        } else if (args[0].equals("enable")) {
            this.isActive = true;
            return Optional.empty();
        } else {
            try {
                int layer = Integer.parseInt(args[0]);
                this.setLayer(layer);
                this.isActive = true;
                return Optional.empty();
            } catch (NumberFormatException e) {
                return Optional.of("Expected \"enable\", \"disable\", or a number");
            }
        }
    }

    @Override
    public List<String> tabCompletionOptions() {
        return List.of("disable", "enable", "0");
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
