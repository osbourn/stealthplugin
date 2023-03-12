package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class DisableHungerHandler extends BooleanSetting implements Listener {
    public DisableHungerHandler() {
        super("disablehunger", true);
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (this.isActive()) {
            if (event.getEntity() instanceof Player) {
                event.getEntity().setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }
}
