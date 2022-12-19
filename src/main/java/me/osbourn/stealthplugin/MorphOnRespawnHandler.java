package me.osbourn.stealthplugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MorphOnRespawnHandler implements Listener, CommandExecutor {
    private boolean isActive = true;
    private final MorphManager morphManager;

    public MorphOnRespawnHandler(MorphManager morphManager) {
        this.morphManager = morphManager;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (this.isActive()) {
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.togglemorphonrespawn")) {
            return false;
        }

        this.setActive(!this.isActive());
        if (this.isActive()) {
            sender.sendMessage("Morph on respawn enabled");
        } else {
            sender.sendMessage("Morph on respawn disabled");
        }
        return true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean state) {
        this.isActive = state;
    }
}