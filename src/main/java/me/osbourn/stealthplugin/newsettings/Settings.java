package me.osbourn.stealthplugin.newsettings;

import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.settingsapi.StringSetting;

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
}
