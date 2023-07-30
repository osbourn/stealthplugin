package me.osbourn.stealthplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class SetupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("red") == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("red");
        }
        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("blue") == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("blue");
        }

        Team redTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("red");
        Team blueTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("blue");
        if (redTeam == null || blueTeam == null) {
            throw new RuntimeException("Failed to register teams");
        }

        redTeam.setColor(ChatColor.RED);
        blueTeam.setColor(ChatColor.BLUE);
        redTeam.setDisplayName("Red");
        blueTeam.setDisplayName("Blue");
        redTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        blueTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        redTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        blueTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

        World overworld = Bukkit.getWorlds().get(0);
        overworld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        overworld.setGameRule(GameRule.DO_FIRE_TICK, false);
        overworld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        overworld.setGameRule(GameRule.NATURAL_REGENERATION, false);
        overworld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);

        return true;
    }
}
