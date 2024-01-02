package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class IncreaseEnvironmentalDamageHandler implements Listener {
    public IncreaseEnvironmentalDamageHandler() {
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Settings.increaseEnvironmentalDamage && event.getEntity() instanceof Player player) {
            DamageCause damageCause = event.getCause();
            if (damageCause != DamageCause.ENTITY_ATTACK
                && damageCause != DamageCause.ENTITY_SWEEP_ATTACK
                && damageCause != DamageCause.PROJECTILE
                && damageCause != DamageCause.MAGIC
                && damageCause != DamageCause.POISON
                && damageCause != DamageCause.WITHER
                && damageCause != DamageCause.THORNS) {
                event.setDamage(event.getDamage() * 2);
            }
        }
    }
}
