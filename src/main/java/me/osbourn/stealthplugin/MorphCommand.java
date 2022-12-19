package me.osbourn.stealthplugin;

import org.bukkit.Bukkit;
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
        if (!sender.hasPermission("stealth.morph")) {
            return false;
        }

        Player player;
        if (args.length == 1) {
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("Executor is not a player!");
                return false;
            }
        } else if (args.length == 2) {
            player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage("No player " + args[1] + " found!");
                return false;
            }
        } else {
            sender.sendMessage("Incorrect number of arguments");
            return false;
        }

        if (morphManager.isPlayerMorphed(player)) {
            sender.sendMessage("Player " + player.getName() + " is already morphed");
            return false;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Entity type " + args[0].toUpperCase() + " unknown");
            return false;
        }

        if (!entityType.isSpawnable() || !entityType.isAlive()) {
            sender.sendMessage("Cannot morph into this entity type");
            return false;
        }
        Entity entity = player.getWorld().spawnEntity(player.getLocation(), entityType);
        LivingEntity livingEntity = (LivingEntity) entity;

        this.morphManager.morphAsEntity(player, livingEntity);
        return true;
    }
}
