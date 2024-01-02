package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.newsettings.Settings;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ClearInventoryOnDeathHandler implements Listener {
    public ClearInventoryOnDeathHandler() {
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Settings.clearInventoryOnDeath) {
            event.getDrops().clear();
        }
    }
}
