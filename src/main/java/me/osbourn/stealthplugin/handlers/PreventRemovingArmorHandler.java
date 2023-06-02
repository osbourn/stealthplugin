package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class PreventRemovingArmorHandler extends BooleanSetting implements Listener {
    public PreventRemovingArmorHandler() {
        super("preventremovingarmor", true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.isActive()) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                event.setCancelled(true);
            }
        }
    }
}
