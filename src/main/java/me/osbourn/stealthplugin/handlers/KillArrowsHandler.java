package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillArrowsHandler extends BooleanSetting implements Listener {
    public KillArrowsHandler() {
        super("killarrows", true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (this.isActive() && event.getDamager().getType() == EntityType.ARROW) {
            event.setDamage(1000);
        }
    }
}
