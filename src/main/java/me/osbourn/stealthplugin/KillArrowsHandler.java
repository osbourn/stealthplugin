package me.osbourn.stealthplugin;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillArrowsHandler extends TogglableHandler {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (this.isActive() && event.getDamager().getType() == EntityType.ARROW) {
            event.setDamage(1000);
        }
    }

    @Override
    protected String description() {
        return "Insta-kill arrows";
    }
}
