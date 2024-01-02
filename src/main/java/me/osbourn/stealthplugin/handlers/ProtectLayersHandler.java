package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.newsettings.Settings;
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

public class ProtectLayersHandler implements Listener {
    /**
     * True if the player should be able to build in the protected layer
     */
    private boolean isPlayerExempt(Player player) {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Settings.protectedLayerEnabled && !this.isPlayerExempt(event.getPlayer())) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= Settings.protectedLayerLevel) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Settings.protectedLayerEnabled && !this.isPlayerExempt(event.getPlayer())) {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlockY() <= Settings.protectedLayerLevel) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Settings.protectedLayerEnabled) {
            event.blockList().removeIf(block -> block.getLocation().getBlockY() <= Settings.protectedLayerLevel);
        }
    }

    /*
     * For things like respawn anchor explosions. TNT explosions are handled under "onEntityExplode".
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (Settings.protectedLayerEnabled) {
            event.blockList().removeIf(block -> block.getLocation().getBlockY() <= Settings.protectedLayerLevel);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (Settings.protectedLayerEnabled) {
            if (event.getBlock().getLocation().getBlockY() <= Settings.protectedLayerLevel) {
                event.setCancelled(true);
            }
        }
    }
}
