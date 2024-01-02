package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.newsettings.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class PreventRemovingArmorHandler implements Listener {
    public PreventRemovingArmorHandler() {
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (Settings.preventRemovingArmor) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                event.setCancelled(true);
            }
        }
    }
}
