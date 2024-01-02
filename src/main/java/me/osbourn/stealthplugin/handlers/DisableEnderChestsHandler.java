package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DisableEnderChestsHandler implements Listener {
    public DisableEnderChestsHandler() {
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (Settings.disableEnderChests) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
