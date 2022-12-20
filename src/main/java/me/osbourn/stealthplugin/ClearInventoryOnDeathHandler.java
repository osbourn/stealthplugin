package me.osbourn.stealthplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ClearInventoryOnDeathHandler extends TogglableHandler {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.isActive()) {
            event.getDrops().clear();
        }
    }

    @Override
    protected String description() {
        return "Clear inventory on death";
    }

    @Override
    protected String permission() {
        return "stealth.toggleclearinventoryondeath";
    }
}
