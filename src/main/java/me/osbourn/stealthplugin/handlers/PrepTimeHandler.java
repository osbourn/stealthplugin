package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class PrepTimeHandler extends BooleanSetting implements Listener {
    private final GameManager gameManager;

    public PrepTimeHandler(GameManager gameManager) {
        super("enforcepreptime", true);
        this.gameManager = gameManager;
    }

    @EventHandler
    public void breakBlockEvent(BlockBreakEvent event) {
        if (this.isActive()) {
            if (this.gameManager.isPrepTime()) {
                if (this.gameManager.isOnAttackers(event.getPlayer())) {
                    event.getPlayer().sendMessage("You can't break blocks during prep time!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        if (this.isActive()) {
            if (this.gameManager.isPrepTime()) {
                event.setCancelled(true);
            }
        }
    }
}
