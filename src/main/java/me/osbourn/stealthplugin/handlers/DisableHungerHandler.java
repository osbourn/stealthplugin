package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.newsettings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class DisableHungerHandler implements Listener {
    public DisableHungerHandler() {
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (Settings.disableHunger) {
            if (event.getEntity() instanceof Player) {
                event.getEntity().setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }
}
