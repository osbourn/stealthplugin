package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.MorphManager;
import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayersDropArrowsHandler implements Listener {
    private final MorphManager morphManager;

    public PlayersDropArrowsHandler(MorphManager morphManager) {
        this.morphManager = morphManager;
    }

    // Needs to have higher priority than ClearInventoryOnDeathHandler, but lower than MorphManager's unmorph on death
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Settings.playersDropArrows && !this.morphManager.isPlayerMorphed(event.getEntity())) {
            ItemStack arrows = new ItemStack(Material.ARROW, 3);
            event.getDrops().add(arrows);
        }
    }
}
