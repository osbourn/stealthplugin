package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.ScoreManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ScoreCommand implements CommandExecutor {
    private final ScoreManager scoreManager;

    public ScoreCommand(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "reset" -> {
                if (args.length != 1) {
                    return false;
                }
                this.scoreManager.resetAllScores();
                sender.sendMessage("All scores reset to 0");
                return true;
            }
            case "set" -> {
                if (args.length != 3) {
                    return false;
                }
                Optional<Team> team = tryParseTeam(args[1]);
                if (team.isPresent()) {
                    try {
                        int newScore = Integer.parseInt(args[2]);
                        this.scoreManager.setScore(team.get(), newScore);
                        return true;
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Unknown number \"" + args[2] + "\"");
                        return false;
                    }
                } else {
                    sender.sendMessage("Unknown team \"" + args[1] + "\"");
                    return false;
                }
            }
            case "get" -> {
                if (args.length != 2) {
                    return false;
                }
                Optional<Team> team = tryParseTeam(args[1]);
                if (team.isPresent()) {
                    int score = this.scoreManager.getScore(team.get());
                    sender.sendMessage(String.format("%s team has %d points", team.get().getName(), score));
                    return true;
                } else {
                    sender.sendMessage("Unknown team \"" + args[1] + "\"");
                    return false;
                }
            }
            case "unregister" -> {
                if (args.length != 2) {
                    return false;
                }
                Optional<Team> team = tryParseTeam(args[1]);
                if (team.isPresent()) {
                    this.scoreManager.unregisterTeam(team.get());
                    sender.sendMessage("Unregistered team " + team.get().getName());
                    return true;
                } else {
                    sender.sendMessage("Unknown team \"" + args[1] + "\"");
                    return false;
                }
            }
            case "getdisplay" -> {
                if (args.length != 1) {
                    return false;
                }
                sender.sendMessage(this.scoreManager.getScoreDisplay());
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private Optional<Team> tryParseTeam(String teamName) {
        return Optional.ofNullable(Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName));
    }
}
