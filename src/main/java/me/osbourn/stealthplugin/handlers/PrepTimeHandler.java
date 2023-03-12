package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PrepTimeHandler extends BooleanSetting implements Listener {
    private final GameManager gameManager;

    public PrepTimeHandler(GameManager gameManager) {
        super("enforcepreptime", true);
        this.gameManager = gameManager;
    }

    @EventHandler
    public void breakBlockEvent(BlockBreakEvent event) {
        if (gameManager.isPrepTime()) {
            if (gameManager.isOnAttackers(event.getPlayer())) {
                event.getPlayer().sendMessage("You can't break blocks during prep time!");
                event.setCancelled(true);
            }
        }
    }
}
