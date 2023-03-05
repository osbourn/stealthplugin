package me.osbourn.stealthplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ClearInventoryOnDeathHandler extends TogglableHandler {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.isActive()) {
            event.getDrops().clear();
        }
    }

    @Override
    protected String description() {
        return "Clear inventory on death";
    }
}
