package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.Color;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Objects;

public class ExplosiveArrowsHandler implements Listener {
    private static final Color EXPLOSIVE_ARROW_COLOR = Color.fromRGB(255, 0, 0);

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (Settings.explosiveArrows && event.getEntity().getType() == EntityType.ARROW) {
            Arrow entity = (Arrow) event.getEntity();
            if (Objects.equals(entity.getColor(), EXPLOSIVE_ARROW_COLOR)) {
                entity.getWorld().createExplosion(entity.getLocation(), 4.0F, false, true,
                        entity.getShooter() instanceof Entity e ? e : null);
                entity.remove();
            }
        }
    }
}
