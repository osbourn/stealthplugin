package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillArrowsHandler implements Listener {
    public KillArrowsHandler() {
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (Settings.killArrows && event.getDamager().getType() == EntityType.ARROW) {
            event.setDamage(1000);
        }
    }
}
