package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.util.GameTargets;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GameObjectivesCommand implements CommandExecutor {
    private final GameManager gameManager;

    public GameObjectivesCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (args.length != 2) {
            return false;
        }

        Material material;
        try {
            material = Material.valueOf(args[1].toUpperCase());
        } catch(IllegalArgumentException e) {
            sender.sendMessage("Unknown material " + args[1].toUpperCase());
            return false;
        }

        GameTargets gameTargets = this.gameManager.getGameTargets();
        if (args[0].equals("add")) {
            if (gameTargets.getTargetMaterials().contains(material)) {
                sender.sendMessage("Objective is already registered as a game target");
                return false;
            } else {
                gameTargets.getTargetMaterials().add(material);
                return true;
            }
        } else if (args[0].equals("remove")) {
            if (gameTargets.getTargetMaterials().contains(material)) {
                gameTargets.getTargetMaterials().remove(material);
                return true;
            } else {
                sender.sendMessage("Objective is not registered as a game target");
                return false;
            }
        } else {
            return false;
        }
    }
}
