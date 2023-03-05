package me.osbourn.stealthplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ClearInventoryOnDeathHandler extends BooleanSetting implements Listener {
    public ClearInventoryOnDeathHandler() {
        super(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.isActive()) {
            event.getDrops().clear();
        }
    }

    @Override
    public String getName() {
        return "clearinventoryondeath";
    }
}
