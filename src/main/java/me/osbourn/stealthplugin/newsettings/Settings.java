package me.osbourn.stealthplugin.newsettings;

import me.osbourn.stealthplugin.util.NullableBlockPosition;

@SuppressWarnings("unused")
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
}
