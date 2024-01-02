package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.MorphManager;
import me.osbourn.stealthplugin.newsettings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeaconRevealHandler implements Listener {
    private final MorphManager morphManager;

    public BeaconRevealHandler(MorphManager morphManager) {
        this.morphManager = morphManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Settings.beaconsRevealPlayers && event.getBlock().getType() == Material.BEACON) {
            if (!this.morphManager.isPlayerMorphed(event.getPlayer())) {
                brieflyRevealPlayers(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        if (Settings.beaconsRevealPlayers) {
            for (Block block : event.blockList()) {
                if (block.getType() == Material.BEACON) {
                    brieflyRevealPlayers(block.getLocation());
                }
            }
        }
    }

    private void brieflyRevealPlayers(Location blockLocation) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!morphManager.isPlayerMorphed(player)) {
                double distanceToBeacon = blockLocation.distance(player.getLocation());
                final double maxDistance = 20;
                if (distanceToBeacon < maxDistance) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 0, false, true, true));
                }
            }
        }
    }
}
