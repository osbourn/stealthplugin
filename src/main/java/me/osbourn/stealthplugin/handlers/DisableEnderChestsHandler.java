package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DisableEnderChestsHandler extends BooleanSetting implements Listener {
    public DisableEnderChestsHandler() {
        super("disableenderchests", true);
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (this.isActive()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
