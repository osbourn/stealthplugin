package me.osbourn.stealthplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class SwapTeamsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("morph.manage")) {
            return false;
        }
        if (args.length != 2) {
            return false;
        }

        Team team1 = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(args[0]);
        Team team2 = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(args[1]);
        if (team1 == null) {
            sender.sendMessage(args[0] + " isn't a valid team");
            return false;
        }
        if (team2 == null) {
            sender.sendMessage(args[1] + " isn't a valid team");
            return false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (team1.hasEntry(player.getName())) {
                team1.removeEntry(player.getName());
                team2.addEntry(player.getName());
            } else if (team2.hasEntry(player.getName())) {
                team2.removeEntry(player.getName());
                team1.addEntry(player.getName());
            }
        }

        return false;
    }
}
