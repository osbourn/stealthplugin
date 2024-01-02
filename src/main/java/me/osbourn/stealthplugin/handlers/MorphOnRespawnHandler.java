package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.MorphManager;
import me.osbourn.stealthplugin.newsettings.Settings;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MorphOnRespawnHandler implements Listener {
    private final MorphManager morphManager;

    public MorphOnRespawnHandler(MorphManager morphManager) {
        this.morphManager = morphManager;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Settings.morphOnRespawn) {
            Player player = event.getPlayer();
            Location loc = event.getRespawnLocation();
            LivingEntity target = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.SILVERFISH);
            if (this.morphManager.isPlayerMorphed(player)) {
                player.sendMessage("Error: Already morphed when respawning");
            } else {
                this.morphManager.morphAsEntity(player, target);
            }
        }
    }
}
