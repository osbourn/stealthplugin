package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.commands.GiveTeamArmorCommand;
import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.StringSetting;
import me.osbourn.stealthplugin.util.GameManagerSettings;
import me.osbourn.stealthplugin.util.GameTargets;
import me.osbourn.stealthplugin.util.MaterialsUtil;
import me.osbourn.stealthplugin.util.ObjectiveDisplayHandler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class GameManager extends BukkitRunnable implements Listener {
    private final StealthPlugin plugin;
    private final MorphManager morphManager;
    private final GameTargets gameTargets;
    private final Scoreboard scoreboard;
    private final Team hideNamesTeam;
    /**
     * The scoreboard objective which is used to render game information.
     * This isn't an actual "objective" in the sense of it being a score, it is just used to draw information in the
     * sidebar.
     */
    private final Objective scoreboardObjective;
    private final ObjectiveDisplayHandler scoreboardObjectiveDisplayHandler;
    private final GameManagerSettings settings;
    private final IntegerSetting timePerRoundSetting;
    private final StringSetting attackingTeamNameSetting;
    private final StringSetting defendingTeamNameSetting;
    private final LocationSetting attackingTeamSpawnLocationSetting;
    private final LocationSetting defendingTeamSpawnLocationSetting;
    private final LocationSetting attackingTeamChestLocationSetting;
    private final LocationSetting defendingTeamChestLocationSetting;

    private int timeRemaining;
    private int prepTimeRemaining;
    private boolean isTimerActive;

    public GameManager(StealthPlugin plugin, MorphManager morphManager, GameTargets gameTargets, GameManagerSettings settings) {
        this.plugin = plugin;
        this.timeRemaining = settings.timePerRoundSetting().getValue();
        this.prepTimeRemaining = 0;
        this.isTimerActive = false;
        this.morphManager = morphManager;
        this.gameTargets = gameTargets;
        this.settings = settings;
        this.timePerRoundSetting = settings.timePerRoundSetting();
        this.attackingTeamNameSetting = settings.attackingTeamNameSetting();
        this.attackingTeamSpawnLocationSetting = settings.attackingTeamSpawnLocationSetting();
        this.defendingTeamNameSetting = settings.defendingTeamNameSetting();
        this.defendingTeamSpawnLocationSetting = settings.defendingTeamSpawnLocationSetting();
        this.attackingTeamChestLocationSetting = settings.attackingTeamChestLocationSetting();
        this.defendingTeamChestLocationSetting = settings.defendingTeamChestLocationSetting();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.hideNamesTeam = this.scoreboard.registerNewTeam("playerteam");
        this.hideNamesTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
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
            if (this.prepTimeRemaining > 0) {
                this.prepTimeRemaining--;
                if (this.prepTimeRemaining == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("The game starts now!"));
                }
            } else {
                if (timeRemaining <= 0) {
                    onTimeUp();
                } else {
                    timeRemaining--;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(this.scoreboard);
        this.hideNamesTeam.addEntry(event.getPlayer().getName());
    }

    private List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();

        if (this.settings.displayGameTargetsSetting().isActive()) {
            for (Material material : this.gameTargets.getTargetMaterials()) {
                String materialName = MaterialsUtil.prettyMaterialName(material.toString());
                boolean hasBeenBroken = this.gameTargets.hasBeenBroken(material);
                if (hasBeenBroken) {
                    lines.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + materialName);
                } else {
                    lines.add(ChatColor.GREEN + materialName);
                }
            }
        }

        if (this.settings.displayPlayerNamesSetting().isActive()) {
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
                //lines.add(ChatColor.BOLD + entry.getKey().getName() + ":");
                for (Player player : entry.getValue()) {
                    if (this.isPlayerEliminated(player)) {
                        lines.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + player.getName());
                    } else {
                        lines.add(color + player.getName());
                    }
                }
            }
        }

        if (this.settings.displayTimeSetting().isActive()) {
            if (this.isPrepTime()) {
                int minutesLeft = prepTimeRemaining / 60;
                int secondsLeft = prepTimeRemaining % 60;
                lines.add(String.format("%sPrep Time: %02d:%02d", ChatColor.YELLOW, minutesLeft, secondsLeft));
            } else {
                int minutesLeft = timeRemaining / 60;
                int secondsLeft = timeRemaining % 60;
                lines.add(String.format("%sTime: %02d:%02d", ChatColor.YELLOW, minutesLeft, secondsLeft));
            }
        }

        return lines;
    }

    private boolean isPlayerEliminated(Player player) {
        return player.getGameMode() == GameMode.SPECTATOR || this.morphManager.isPlayerMorphed(player) || player.isDead();
    }

    private void onTimeUp() {
        announceMessage("Time's Up!");
        this.isTimerActive = false;
    }

    private void readyPlayers() {
        World overworld = Bukkit.getWorlds().get(0);
        Location attackersSpawnLocation = this.attackingTeamSpawnLocationSetting.toLocationInWorld(overworld);
        Location defendersSpawnLocation = this.defendingTeamSpawnLocationSetting.toLocationInWorld(overworld);
        Location attackingTeamChestLocation = this.attackingTeamChestLocationSetting.toLocationInWorld(overworld);
        Location defendingTeamChestLocation = this.defendingTeamChestLocationSetting.toLocationInWorld(overworld);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.isOnAttackers(player)) {
                if (this.isLocationSet(this.attackingTeamSpawnLocationSetting)) {
                    player.teleport(attackersSpawnLocation);
                    player.setBedSpawnLocation(attackersSpawnLocation, true);
                }
                if (this.isLocationSet(this.attackingTeamChestLocationSetting)) {
                    this.copyChestToPlayer(attackingTeamChestLocation, player);
                }
            } else if (this.isOnDefenders(player)) {
                if (this.isLocationSet(this.defendingTeamSpawnLocationSetting)) {
                    player.teleport(defendersSpawnLocation);
                    player.setBedSpawnLocation(attackersSpawnLocation, true);
                }
                if (this.isLocationSet(this.defendingTeamChestLocationSetting)) {
                    this.copyChestToPlayer(defendingTeamChestLocation, player);
                }
            }

            if (morphManager.isPlayerMorphed(player)) {
                morphManager.unmorph(player);
            }
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            player.setHealth(20.0);

            if (this.settings.applyInvisibilityOnStart().isActive()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0, false, false, false));
            }
        }

        GiveTeamArmorCommand.giveTeamArmor();
    }

    public boolean isOnAttackers(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        return team != null && team.getName().equals(this.attackingTeamNameSetting.getValue());
    }

    public boolean isOnDefenders(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        return team != null && team.getName().equals(this.defendingTeamNameSetting.getValue());
    }

    private void copyChestToPlayer(Location chestLocation, Player player) {
        if (chestLocation.getBlock().getType() != Material.CHEST) {
            player.sendMessage("Failed to give starting items, because specified location wasn't a chest (please contact admin)");
            return;
        }

        Block block = chestLocation.getBlock();
        BlockState blockState = block.getState();
        if (!(blockState instanceof Chest chest)) {
            player.sendMessage("Failed to give starting items, because chest doesn't have chest data (please contact admin)");
            return;
        }

        Inventory inventory = chest.getBlockInventory();
        player.getInventory().clear();
        int i = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                player.getInventory().setItem(i, itemStack.clone());
            }
            i++;
            if (i > 35) {
                player.sendMessage("Ran out of inventory space (please contact admin)");
            }
        }
    }

    public boolean isPrepTime() {
        return this.prepTimeRemaining > 0;
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
        this.prepTimeRemaining = this.settings.prepTimeSetting().getValue();
        this.isTimerActive = true;
        this.gameTargets.resetBrokenTargets();
        this.readyPlayers();
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    private void announceMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }
}
