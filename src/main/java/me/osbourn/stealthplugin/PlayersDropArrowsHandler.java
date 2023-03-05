package me.osbourn.stealthplugin;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayersDropArrowsHandler extends TogglableHandler {
    private final MorphManager morphManager;

    public PlayersDropArrowsHandler(MorphManager morphManager) {
        super();
        this.morphManager = morphManager;
    }

    // Needs to have higher priority than ClearInventoryOnDeathHandler, but lower than MorphManager's unmorph on death
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.isActive() && !this.morphManager.isPlayerMorphed(event.getEntity())) {
            ItemStack arrows = new ItemStack(Material.ARROW, 3);
            event.getDrops().add(arrows);
        }
    }

    @Override
    protected String description() {
        return "Players drop arrows";
    }
}
