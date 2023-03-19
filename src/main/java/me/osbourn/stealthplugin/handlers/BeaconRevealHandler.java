package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.MorphManager;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeaconRevealHandler extends BooleanSetting implements Listener {
    private final MorphManager morphManager;

    public BeaconRevealHandler(MorphManager morphManager) {
        super("beaconsrevealplayers", false);
        this.morphManager = morphManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isActive() && event.getBlock().getType() == Material.BEACON) {
            if (!this.morphManager.isPlayerMorphed(event.getPlayer())) {
                brieflyRevealPlayers();
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        if (this.isActive()) {
            for (Block block : event.blockList()) {
                if (block.getType() == Material.BEACON) {
                    brieflyRevealPlayers();
                }
            }
        }
    }

    private void brieflyRevealPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 0, false, true, true));
        }
    }
}
