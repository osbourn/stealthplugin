package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settingsapi.Setting;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.Optional;

public class ProtectLayersHandler implements Setting, Listener {
    private int layer = 0;
    private boolean isActive = false;

    /**
     * True if the player should be able to build in the protected layer
     */
    private boolean isPlayerExempt(Player player) {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isActive && !this.isPlayerExempt(event.getPlayer())) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= this.getLayer()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isActive && !this.isPlayerExempt(event.getPlayer())) {
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

    /*
     * For things like respawn anchor explosions. TNT explosions are handled under "onEntityExplode".
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
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

    @Override
    public Object configValue() {
        return String.format("%b,%d", this.isActive, this.getLayer());
    }

    @Override
    public void setFromConfigValue(Object value) {
        if (value instanceof String s) {
            String[] words = s.split(",");
            if (words.length == 2) {
                try {
                    // Declare variables before setting so that if the number is an invalid format it doesn't set only the boolean
                    boolean isActive = Boolean.parseBoolean(words[0]);
                    int layer = Integer.parseInt(words[1]);

                    this.isActive = isActive;
                    this.layer = layer;
                } catch (NumberFormatException e) {
                    // TODO: Log error
                }
            }
        }
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
