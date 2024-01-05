package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Arrays;

public class IncreaseEnvironmentalDamageHandler implements Listener {
    public IncreaseEnvironmentalDamageHandler() {
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Settings.increaseEnvironmentalDamage && event.getEntity() instanceof Player player) {
            DamageCause damageCause = event.getCause();
            DamageCause[] affectedDamageTypes = {
                    DamageCause.CONTACT,
                    //DamageCause.ENTITY_ATTACK,
                    //DamageCause.ENTITY_SWEEP_ATTACK,
                    //DamageCause.PROJECTILE,
                    DamageCause.SUFFOCATION,
                    DamageCause.FALL,
                    DamageCause.FIRE,
                    DamageCause.FIRE_TICK,
                    //DamageCause.MELTING,
                    DamageCause.LAVA,
                    DamageCause.DROWNING,
                    DamageCause.BLOCK_EXPLOSION,
                    DamageCause.ENTITY_EXPLOSION,
                    //DamageCause.VOID,
                    DamageCause.LIGHTNING,
                    //DamageCause.SUICIDE,
                    //DamageCause.STARVATION,
                    //DamageCause.POISON,
                    //DamageCause.MAGIC,
                    //DamageCause.WITHER,
                    DamageCause.FALLING_BLOCK,
                    //DamageCause.THORNS,
                    DamageCause.DRAGON_BREATH,
                    //DamageCause.CUSTOM,
                    DamageCause.FLY_INTO_WALL,
                    DamageCause.HOT_FLOOR,
                    //DamageCause.CRAMMING,
                    //DamageCause.DRYOUT,
                    DamageCause.FREEZE,
            };
            if (Arrays.asList(affectedDamageTypes).contains(damageCause)) {
                double multiplier = Settings.environmentalDamagePercentIncrease / 100.0 + 1.0;
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }
}
