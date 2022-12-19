package me.osbourn.stealthplugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MorphCommand implements CommandExecutor {
    private final MorphManager morphManager;
    public MorphCommand(MorphManager morphManager) {
        this.morphManager = morphManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        Location loc = player.getLocation();
        World world = player.getWorld();
        Entity entity = world.spawnEntity(loc, EntityType.SILVERFISH);
        LivingEntity livingEntity = (LivingEntity) entity;

        this.morphManager.morphAsEntity(player, livingEntity);
        return true;
    }
}
