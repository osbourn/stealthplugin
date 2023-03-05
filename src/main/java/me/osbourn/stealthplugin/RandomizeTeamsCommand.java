package me.osbourn.stealthplugin;

import java.sql.Array;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class RandomizeTeamsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        var teamsWithPlayerCount = new ArrayList<Map.Entry<Team, Integer>>();
        //var teamsWithoutPlayerCount = new ArrayList<Team>();
        for (String arg : args) {
            if (arg.contains(":")) {
                Team team = scoreboard.getTeam(arg.split(":")[0]);
                if (team == null) {
                    sender.sendMessage("Unknown team: " + arg.split(":")[0]);
                    return false;
                }
                int count;
                try {
                    count = Integer.parseInt(arg.split(":")[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid number: " + arg.split(":")[1]);
                    return false;
                }
                teamsWithPlayerCount.add(new SimpleEntry<>(team, count));
            } else {
                /*
                Team team = scoreboard.getTeam(arg);
                if (team == null) {
                    sender.sendMessage("Unknown team: " + arg);
                    return false;
                }
                teamsWithoutPlayerCount.add(team);
                */
                sender.sendMessage("Please specify the number of players for each team");
                return false;
            }
        }

        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(playerList);
        for (var entry : teamsWithPlayerCount) {
            if (playerList.size() < entry.getValue()) {
                sender.sendMessage("Too many players requested!");
                return false;
            }

            // Add the first n entries from the playerList to the specified team, and remove them from the list
            // This is a bit inefficient, since it is removing from the start rather than end of the list, but this
            // can be changed if it actually becomes a problem
            IntStream.range(0, entry.getValue())
                    .forEach(i -> entry.getKey().addEntry(playerList.remove(0).getName()));
        }

        sender.sendMessage("Players have been assigned to teams, with " + playerList.size() + " players left unassigned");
        return true;
    }
}
