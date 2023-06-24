package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.commands.GiveTeamArmorCommand;
import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.StringSetting;
import me.osbourn.stealthplugin.util.GameManagerSettings;
import me.osbourn.stealthplugin.util.GameTargets;
import me.osbourn.stealthplugin.util.MaterialsUtil;
import me.osbourn.stealthplugin.util.ObjectiveDisplayHandler;
import net.md_5.bungee.api.chat.BaseComponent;
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
    private enum GameResult {
        ATTACKER_VICTORY,
        DEFENDER_VICTORY,
        DRAW
    }

    private final StealthPlugin plugin;
    private final MorphManager morphManager;
    private final ScoreManager scoreManager;
    private final KitManager kitManager;
    private final GameTargets gameTargets;

    /**
     * Alternate scoreboard used when a player runs the /togglesb command to hide the scoreboard
     * It adds all players to the same team so that it doesn't show nametags to anyone
     */
    private final Scoreboard alternateScoreboard;
    private final Team playerTeam;

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

    public GameManager(StealthPlugin plugin, MorphManager morphManager, ScoreManager scoreManager,
                       KitManager kitManager, GameTargets gameTargets, GameManagerSettings settings) {
        this.plugin = plugin;
        this.timeRemaining = settings.timePerRoundSetting().getValue();
        this.prepTimeRemaining = -1;
        this.isTimerActive = false;
        this.morphManager = morphManager;
        this.scoreManager = scoreManager;
        this.kitManager = kitManager;
        this.gameTargets = gameTargets;
        this.settings = settings;
        this.timePerRoundSetting = settings.timePerRoundSetting();
        this.attackingTeamNameSetting = settings.attackingTeamNameSetting();
        this.attackingTeamSpawnLocationSetting = settings.attackingTeamSpawnLocationSetting();
        this.defendingTeamNameSetting = settings.defendingTeamNameSetting();
        this.defendingTeamSpawnLocationSetting = settings.defendingTeamSpawnLocationSetting();
        this.attackingTeamChestLocationSetting = settings.attackingTeamChestLocationSetting();
        this.defendingTeamChestLocationSetting = settings.defendingTeamChestLocationSetting();
        this.alternateScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.playerTeam = this.alternateScoreboard.registerNewTeam("playerteam");
        this.playerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.playerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        this.playerTeam.setCanSeeFriendlyInvisibles(false);
        // Prevent errors when the objective already exists (e.g. when starting the server for a second time)
        if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("stealthgame") != null) {
            Bukkit.getScoreboardManager().getMainScoreboard().getObjective("stealthgame").unregister();
        }
        this.scoreboardObjective = Bukkit.getScoreboardManager().getMainScoreboard()
                .registerNewObjective("stealthgame", "dummy",
                        ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Game Info");
        this.scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboardObjectiveDisplayHandler = new ObjectiveDisplayHandler(this.scoreboardObjective);

        this.attackersTeam().ifPresent(team -> this.scoreManager.setScore(team, 0));
        this.defendersTeam().ifPresent(team -> this.scoreManager.setScore(team, 0));
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
            if (this.prepTimeRemaining >= 0) {
                if (this.prepTimeRemaining == 0) {
                    List<String> addedTargets = this.gameTargets.fillRemainingTargetSlots(
                            this.settings.numberOfTargetsSetting().getValue());
                    if (!addedTargets.isEmpty()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (this.isOnDefenders(player)) {
                                player.sendMessage(String.format(
                                        "%sNot all targets were selected, so the following were added at random: %s",
                                        ChatColor.GRAY, String.join(", ", addedTargets)));
                            }
                        }
                    }
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("The game starts now!"));
                }
                this.prepTimeRemaining--;
            } else {
                checkForVictory();
                if (this.isTimerActive) {
                    timeRemaining--;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // event.getPlayer().setScoreboard(this.alternateScoreboard);
        this.playerTeam.addEntry(event.getPlayer().getName());
    }

    private List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();

        if (this.settings.displayScoreSetting().isActive()) {
            lines.add(this.scoreManager.getScoreDisplay());
        }

        if (this.settings.displayGameTargetsSetting().isActive() && !this.isPrepTime()) {
            for (Material material : this.gameTargets.getActiveTargets()) {
                String materialName = MaterialsUtil.prettyMaterialName(material.toString());
                boolean hasBeenBroken = this.gameTargets.hasBeenBroken(material);
                if (hasBeenBroken) {
                    lines.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + materialName);
                } else {
                    lines.add(ChatColor.GREEN + materialName);
                }
            }
        }

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
            if (this.settings.displayTeamsSetting().isActive()) {
                int numAlive = (int) entry.getValue().stream().filter(p -> !this.isPlayerEliminated(p)).count();
                lines.add(String.format("%s%s%s:%s %d alive", entry.getKey().getColor(), ChatColor.BOLD, entry.getKey().getDisplayName(), ChatColor.RESET, numAlive));
            }

            if (this.settings.displayPlayerNamesSetting().isActive()) {
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

    private void checkForVictory() {
        boolean attackersAlive = false;
        boolean defendersAlive = false;
        boolean timeLeft = this.timeRemaining > 0;
        boolean allTargetsBroken = this.gameTargets.allTargetsBroken();

        if (!timeLeft) {
            announceMessage("Time's up");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.isPlayerEliminated(player)) {
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
                if (team != null) {
                    if (team.getName().equals(this.settings.attackingTeamNameSetting().getValue())) {
                        attackersAlive = true;
                    }
                    if (team.getName().equals(this.settings.defendingTeamNameSetting().getValue())) {
                        defendersAlive = true;
                    }
                }
            }
        }

        boolean attackersMeetWinCondition = !defendersAlive || allTargetsBroken;
        boolean defendersMeetWinCondition = !attackersAlive || !timeLeft;

        if (attackersMeetWinCondition && !defendersMeetWinCondition) {
            declareWinner(GameResult.ATTACKER_VICTORY);
        } else if (defendersMeetWinCondition && !attackersMeetWinCondition) {
            declareWinner(GameResult.DEFENDER_VICTORY);
        } else if (attackersMeetWinCondition) {
            declareWinner(GameResult.DRAW);
        }
    }

    private void declareWinner(GameResult gameResult) {
        this.isTimerActive = false;

        String title = "";
        switch (gameResult) {
            case ATTACKER_VICTORY -> {
                title = "Attackers Win!";
                Team team = Bukkit.getScoreboardManager().getMainScoreboard()
                        .getTeam(this.settings.attackingTeamNameSetting().getValue());
                if (team != null) {
                    title = team.getColor() + title;
                }

                this.attackersTeam().ifPresent(this.scoreManager::incrementScore);
            }
            case DEFENDER_VICTORY -> {
                title = "Defenders Win!";
                Team team = Bukkit.getScoreboardManager().getMainScoreboard()
                        .getTeam(this.settings.defendingTeamNameSetting().getValue());
                if (team != null) {
                    title = team.getColor() + title;
                }

                this.defendersTeam().ifPresent(this.scoreManager::incrementScore);
            }
            case DRAW -> title = "It's a draw!";
        }

        String subtitle = this.scoreManager.getScoreDisplay();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(title, subtitle, 10, 70, 20);
        }
    }

    private void readyPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            readyPlayer(player);
        }
    }

    public void readyPlayer(Player player) {
        World overworld = Bukkit.getWorlds().get(0);
        Location attackersSpawnLocation = this.attackingTeamSpawnLocationSetting.toLocationInWorld(overworld);
        Location defendersSpawnLocation = this.defendingTeamSpawnLocationSetting.toLocationInWorld(overworld);
        Location attackingTeamChestLocation = this.attackingTeamChestLocationSetting.toLocationInWorld(overworld);
        Location defendingTeamChestLocation = this.defendingTeamChestLocationSetting.toLocationInWorld(overworld);
        Location respawnLocation = this.settings.respawnLocationSetting().toLocationInWorld(overworld);

        if (this.isOnAttackers(player)) {
            if (this.isLocationSet(this.attackingTeamSpawnLocationSetting)) {
                player.teleport(attackersSpawnLocation);
            }
            if (this.isLocationSet(this.settings.respawnLocationSetting())) {
                player.setBedSpawnLocation(respawnLocation, true);
            }
            player.getInventory().clear();
            GiveTeamArmorCommand.giveTeamArmor(player);
            this.kitManager.givePlayerAttackingKit(player);
            if (this.isLocationSet(this.attackingTeamChestLocationSetting)) {
                this.copyChestToPlayer(attackingTeamChestLocation, player);
            }
        } else if (this.isOnDefenders(player)) {
            if (this.isLocationSet(this.defendingTeamSpawnLocationSetting)) {
                player.teleport(defendersSpawnLocation);
            }
            if (this.isLocationSet(this.settings.respawnLocationSetting())) {
                player.setBedSpawnLocation(respawnLocation, true);
            }
            player.getInventory().clear();
            GiveTeamArmorCommand.giveTeamArmor(player);
            this.kitManager.givePlayerDefendingKit(player);
            if (this.isLocationSet(this.defendingTeamChestLocationSetting)) {
                this.copyChestToPlayer(defendingTeamChestLocation, player);
            }
        }

        if (morphManager.isPlayerMorphed(player)) {
            morphManager.unmorph(player);
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        player.setHealth(20.0);
        player.setFireTicks(0);
        player.setArrowsInBody(0);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setFallDistance(0.0F);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (this.settings.applyInvisibilityOnStart().isActive()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 1000000, 0, false, false, false));
        }
    }

    public void sendPlayersToLobby() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isLocationSet(this.settings.lobbyLocationSetting())) {
                World overworld = Bukkit.getWorlds().get(0);
                player.teleport(this.settings.lobbyLocationSetting().toLocationInWorld(overworld));
            }
            if (morphManager.isPlayerMorphed(player)) {
                morphManager.unmorph(player);
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            player.setHealth(20.0);

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 1000000, 4, false, false, false));
        }

        GiveTeamArmorCommand.giveAllPlayersTeamArmor();
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
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                player.getInventory().addItem(itemStack.clone());
            }
        }
    }

    public boolean isPrepTime() {
        return this.prepTimeRemaining >= 0;
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
        this.gameTargets.resetSelectedTargets();
        this.readyPlayers();

        BaseComponent[] targetSelectionPrompt = this.gameTargets.getSelectionMessage();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.isOnDefenders(player)) {
                player.spigot().sendMessage(targetSelectionPrompt);
            }
        }
    }

    /**
     * Get alternate scoreboard used for the /togglesb command
     */
    public Scoreboard getAlternateScoreboard() {
        return this.alternateScoreboard;
    }

    public GameManagerSettings getSettings() {
        return this.settings;
    }

    public GameTargets getGameTargets() {
        return this.gameTargets;
    }

    private void announceMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    private Optional<Team> attackersTeam() {
        return Optional.ofNullable(Bukkit.getScoreboardManager().getMainScoreboard()
                .getTeam(this.settings.attackingTeamNameSetting().getValue()));
    }

    private Optional<Team> defendersTeam() {
        return Optional.ofNullable(Bukkit.getScoreboardManager().getMainScoreboard()
                .getTeam(this.settings.defendingTeamNameSetting().getValue()));
    }
}
