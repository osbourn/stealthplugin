package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.StringSetting;
import me.osbourn.stealthplugin.util.ObjectiveDisplayHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager extends BukkitRunnable implements Listener {
    private final StealthPlugin plugin;
    private final MorphManager morphManager;
    private final Scoreboard scoreboard;
    /**
     * The scoreboard objective which is used to render game information.
     * This isn't an actual "objective" in the sense of it being a score, it is just used to draw information in the
     * sidebar.
     */
    private final Objective scoreboardObjective;
    private final ObjectiveDisplayHandler scoreboardObjectiveDisplayHandler;
    private final IntegerSetting timePerRoundSetting;
    private final StringSetting attackingTeamNameSetting;
    private final StringSetting defendingTeamNameSetting;
    private final LocationSetting attackingTeamSpawnPointSetting;
    private final LocationSetting defendingTeamSpawnPointSetting;

    private int timeRemaining;
    private boolean isTimerActive;

    public GameManager(StealthPlugin plugin, MorphManager morphManager, IntegerSetting timePerRoundSetting,
                       StringSetting attackingTeamNameSetting, LocationSetting attackingTeamSpawnPointSetting,
                       StringSetting defendingTeamNameSetting, LocationSetting defendingTeamSpawnPointSetting) {
        this.plugin = plugin;
        this.timeRemaining = 600;
        this.isTimerActive = false;
        this.morphManager = morphManager;
        this.timePerRoundSetting = timePerRoundSetting;
        this.attackingTeamNameSetting = attackingTeamNameSetting;
        this.attackingTeamSpawnPointSetting = attackingTeamSpawnPointSetting;
        this.defendingTeamNameSetting = defendingTeamNameSetting;
        this.defendingTeamSpawnPointSetting = defendingTeamSpawnPointSetting;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreboardObjective = this.scoreboard.registerNewObjective("stealthgame", "dummy",
                ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Game Info");
        this.scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboardObjectiveDisplayHandler = new ObjectiveDisplayHandler(this.scoreboardObjective);
    }

    public boolean isTimerActive() {
        return this.isTimerActive;
    }

    public void setTimerActive(boolean timerActive) {
        this.isTimerActive = timerActive;
    }

    @Override
    public void run() {
        this.scoreboardObjectiveDisplayHandler.updateObjective(getScoreboardLines());

        if (this.isTimerActive) {
            if (timeRemaining <= 0) {
                onTimeUp();
            } else {
                timeRemaining--;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(this.scoreboard);
    }

    private List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();

        Map<Team, List<Player>> teams = new HashMap<>();
        // TODO: Consider rendering players not on teams
        List<Player> playersWithoutTeams = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (team == null) {
                playersWithoutTeams.add(player);
            } else {
                if (!teams.containsKey(team)) {
                    teams.put(team, new ArrayList<>());
                }
                teams.get(team).add(player);
            }
        }
        for (Map.Entry<Team, List<Player>> entry : teams.entrySet()) {
            ChatColor color = entry.getKey().getColor();
            lines.add(ChatColor.BOLD + entry.getKey().getName() + ":");
            for (Player player : entry.getValue()) {
                if (this.isPlayerEliminated(player)) {
                    lines.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + player.getName());
                } else {
                    lines.add(color + player.getName());
                }
            }
        }

        int minutesLeft = timeRemaining / 60;
        int secondsLeft = timeRemaining % 60;
        lines.add(String.format("%sTime: %02d:%02d", ChatColor.YELLOW, minutesLeft, secondsLeft));
        lines.add(String.format("%s/togglesb to hide", ChatColor.GRAY));

        return lines;
    }

    private boolean isPlayerEliminated(Player player) {
        return player.getGameMode() == GameMode.SPECTATOR || this.morphManager.isPlayerMorphed(player) || player.isDead();
    }

    private void onTimeUp() {
        announceMessage("Time's Up!");
        this.isTimerActive = false;
    }

    private void movePlayersToSpawnPoints() {
        World overworld = Bukkit.getWorlds().get(0);

        // TODO: Eliminate duplicate code
        if (isLocationSet(this.attackingTeamSpawnPointSetting)) {
            Location location = this.attackingTeamSpawnPointSetting.toLocationInWorld(overworld);
            // Having one loop per team could be made more efficient, but it's fine for now
            for (Player player : Bukkit.getOnlinePlayers()) {
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
                if (team != null && team.getName().equals(this.attackingTeamNameSetting.getValue())) {
                    player.teleport(location);
                }
            }
        }

        if (isLocationSet(this.defendingTeamSpawnPointSetting)) {
            Location location = this.defendingTeamSpawnPointSetting.toLocationInWorld(overworld);
            for (Player player : Bukkit.getOnlinePlayers()) {
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
                if (team != null && team.getName().equals(this.defendingTeamNameSetting.getValue())) {
                    player.teleport(location);
                }
            }
        }
    }

    private boolean isLocationSet(LocationSetting setting) {
        // TODO: Better way of having unset locations
        return setting.x() != 0 || setting.y() != 0 || setting.z() != 0;
    }

    /**
     * Start or reset the game
     */
    public void resetGame() {
        this.timeRemaining = this.timePerRoundSetting.getValue();
        this.isTimerActive = true;
        this.movePlayersToSpawnPoints();
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    private void announceMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }
}
