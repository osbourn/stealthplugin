package me.osbourn.stealthplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class UnmorphCommand implements CommandExecutor {
    private final MorphManager morphManager;
    public UnmorphCommand(MorphManager morphManager) {
        this.morphManager = morphManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }
        Player player;
        if (args.length == 0) {
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("Executor is not a player!");
                return false;
            }
        } else if (args.length == 1) {
            if (args[0].equals("*")) {
                unmorphAllPlayers();
                return true;
            }
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("No player " + args[0] + " found!");
                return false;
            }
        } else {
            sender.sendMessage("Too many arguments");
            return false;
        }

        if (this.morphManager.isPlayerMorphed(player)) {
            this.morphManager.unmorph(player);
            return true;
        } else {
            sender.sendMessage("Player was not morphed");
            return false;
        }
    }

    private void unmorphAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.morphManager.isPlayerMorphed(player)) {
                this.morphManager.unmorph(player);
            }
        }
    }
}
