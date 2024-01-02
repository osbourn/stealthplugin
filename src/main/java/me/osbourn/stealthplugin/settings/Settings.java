package me.osbourn.stealthplugin.settings;

import me.osbourn.stealthplugin.util.NullableBlockPosition;

public final class Settings {
    private Settings() {
    }

    @Setting(name = "timePerRound")
    public static int timePerRound = 300;
    @Setting(name = "prepTime")
    public static int prepTime = 30;
    @Setting(name = "displayGameTargetsOnScoreboard")
    public static boolean displayGameTargetsOnScoreboard = true;
    @Setting(name = "displayTeamsOnScoreboard")
    public static boolean displayTeamsOnScoreboard = false;
    @Setting(name = "displayPlayerNamesOnScoreboard")
    public static boolean displayPlayerNamesOnScoreboard = true;
    @Setting(name = "displayTimeOnScoreboard")
    public static boolean displayTimeOnScoreboard = true;
    @Setting(name = "displayScoreOnScoreboard")
    public static boolean displayScoreOnScoreboard = false;
    @Setting(name = "applyInvisibilityOnStart")
    public static boolean applyInvisibilityOnStart = false;
    @Setting(name = "attackingTeamName")
    public static String attackingTeamName = "red";
    @Setting(name = "defendingTeamName")
    public static String defendingTeamName = "blue";
    @Setting(name = "attackingTeamSpawnLocation")
    public static NullableBlockPosition attackingTeamSpawnLocation = NullableBlockPosition.UNSET;
    @Setting(name = "defendingTeamSpawnLocation")
    public static NullableBlockPosition defendingTeamSpawnLocation = NullableBlockPosition.UNSET;
    @Setting(name = "attackingTeamChestLocation")
    public static NullableBlockPosition attackingTeamChestLocation = NullableBlockPosition.UNSET;
    @Setting(name = "defendingTeamChestLocation")
    public static NullableBlockPosition defendingTeamChestLocation = NullableBlockPosition.UNSET;
    @Setting(name = "respawnLocation")
    public static NullableBlockPosition respawnLocation = NullableBlockPosition.UNSET;
    @Setting(name = "lobbyLocation")
    public static NullableBlockPosition lobbyLocation = NullableBlockPosition.UNSET;
    @Setting(name = "numberOfTargets")
    public static int numberOfTargets = 2;
    @Setting(name = "morphedPlayersCanAttack")
    public static boolean morphedPlayersCanAttack = false;
    @Setting(name = "morphedPlayersIgnoreArrows")
    public static boolean morphedPlayersIgnoreArrows = true;
    @Setting(name = "structurePasteLocation")
    public static NullableBlockPosition structurePasteLocation = NullableBlockPosition.UNSET;
    @Setting(name = "killEntitiesBeforePaste")
    public static boolean killEntitiesBeforePaste = true;
    @Setting(name = "killArrows")
    public static boolean killArrows = true;
    @Setting(name = "clearInventoryOnDeath")
    public static boolean clearInventoryOnDeath = true;
    @Setting(name = "protectedLayerEnabled")
    public static boolean protectedLayerEnabled = false;
    @Setting(name = "protectedLayerLevel")
    public static int protectedLayerLevel = 0;
    @Setting(name = "disableEnderChests")
    public static boolean disableEnderChests = true;
    @Setting(name = "disableHunger")
    public static boolean disableHunger = true;
    @Setting(name = "preventRemovingArmor")
    public static boolean preventRemovingArmor = true;
    @Setting(name = "increaseEnvironmentalDamage")
    public static boolean increaseEnvironmentalDamage = true;
    @Setting(name = "announceBeacons")
    public static boolean announceBeacons = false;
    @Setting(name = "beaconsRevealPlayers")
    public static boolean beaconsRevealPlayers = true;
    @Setting(name = "morphOnRespawn")
    public static boolean morphOnRespawn = true;
    @Setting(name = "playersDropArrows")
    public static boolean playersDropArrows = true;
    @Setting(name = "announceBrokenTargets")
    public static boolean announceBrokenTargets = true;
    @Setting(name = "enforcePrepTime")
    public static boolean enforcePrepTime = true;
    @Setting(name = "preventPrematureTargetDestruction")
    public static boolean preventPrematureTargetDestruction = true;
    @Setting(name = "glowingTeammates")
    public static boolean glowingTeammates = false;
    @Setting(name = "timeInLobby")
    public static int timeInLobby = 30;
}
