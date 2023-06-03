package me.osbourn.stealthplugin.util;

import me.osbourn.stealthplugin.settingsapi.*;

import java.util.List;

public record GameManagerSettings(IntegerSetting timePerRoundSetting, IntegerSetting prepTimeSetting,
                                  BooleanSetting displayGameTargetsSetting, BooleanSetting displayTeamsSetting,
                                  BooleanSetting displayPlayerNamesSetting, BooleanSetting displayTimeSetting,
                                  BooleanSetting displayScoreSetting,
                                  BooleanSetting applyInvisibilityOnStart,
                                  StringSetting attackingTeamNameSetting, StringSetting defendingTeamNameSetting,
                                  LocationSetting attackingTeamSpawnLocationSetting,
                                  LocationSetting defendingTeamSpawnLocationSetting,
                                  LocationSetting attackingTeamChestLocationSetting,
                                  LocationSetting defendingTeamChestLocationSetting,
                                  LocationSetting respawnLocationSetting) {

    public static GameManagerSettings makeNew() {
        IntegerSetting timePerRoundSetting = new IntegerSetting("timeperround", 300);
        IntegerSetting prepTimeSetting = new IntegerSetting("preptime", 30);
        BooleanSetting displayGameTargetsSetting = new BooleanSetting("displaygametargets", true);
        BooleanSetting displayTeamsSetting = new BooleanSetting("displayteams", false);
        BooleanSetting displayPlayerNamesSetting = new BooleanSetting("displayplayernames", true);
        BooleanSetting displayTimeSetting = new BooleanSetting("displaytime", true);
        BooleanSetting displayScoreSetting = new BooleanSetting("displayscore", false);
        BooleanSetting applyInvisibilityOnStart = new BooleanSetting("applyinvisibilityonstart", false);
        StringSetting attackingTeamNameSetting = new StringSetting("attackingteamname", "red");
        StringSetting defendingTeamNameSetting = new StringSetting("defendingteamname", "blue");
        LocationSetting attackingTeamSpawnLocationSetting = new LocationSetting("attackingteamspawnlocation", 0, 0, 0);
        LocationSetting defendingTeamSpawnLocationSetting = new LocationSetting("defendingteamspawnlocation", 0, 0, 0);
        LocationSetting attackingTeamChestLocationSetting = new LocationSetting("attackingteamchestlocation", 0, 0, 0);
        LocationSetting defendingTeamChestLocationSetting = new LocationSetting("defendingteamchestlocation", 0, 0, 0);
        LocationSetting respawnLocationSetting = new LocationSetting("respawnlocation", 0, 0, 0);
        return new GameManagerSettings(timePerRoundSetting, prepTimeSetting, displayGameTargetsSetting,
                displayTeamsSetting,
                displayPlayerNamesSetting, displayTimeSetting,
                displayScoreSetting,
                applyInvisibilityOnStart,
                attackingTeamNameSetting, defendingTeamNameSetting,
                attackingTeamSpawnLocationSetting, defendingTeamSpawnLocationSetting,
                attackingTeamChestLocationSetting, defendingTeamChestLocationSetting,
                respawnLocationSetting);
    }

    public void addAllTo(List<Setting> settingsList) {
        settingsList.add(timePerRoundSetting);
        settingsList.add(prepTimeSetting);
        settingsList.add(displayGameTargetsSetting);
        settingsList.add(displayTeamsSetting);
        settingsList.add(displayPlayerNamesSetting);
        settingsList.add(displayTimeSetting);
        settingsList.add(displayScoreSetting);
        settingsList.add(applyInvisibilityOnStart);
        settingsList.add(attackingTeamNameSetting);
        settingsList.add(defendingTeamNameSetting);
        settingsList.add(attackingTeamSpawnLocationSetting);
        settingsList.add(defendingTeamSpawnLocationSetting);
        settingsList.add(attackingTeamChestLocationSetting);
        settingsList.add(defendingTeamChestLocationSetting);
        settingsList.add(respawnLocationSetting);
    }
}
