package me.osbourn.stealthplugin.util;

import me.osbourn.stealthplugin.settingsapi.*;

import java.util.List;

public record GameManagerSettings(IntegerSetting timePerRoundSetting, IntegerSetting prepTimeSetting,
                                  BooleanSetting displayGameTargetsSetting,
                                  BooleanSetting displayPlayerNamesSetting, BooleanSetting displayTimeSetting,
                                  StringSetting attackingTeamNameSetting, StringSetting defendingTeamNameSetting,
                                  LocationSetting attackingTeamSpawnLocationSetting, LocationSetting defendingTeamSpawnLocationSetting,
                                  LocationSetting attackingTeamChestLocationSetting, LocationSetting defendingTeamChestLocationSetting) {

    public static GameManagerSettings makeNew() {
        IntegerSetting timePerRoundSetting = new IntegerSetting("timeperround", 600);
        IntegerSetting prepTimeSetting = new IntegerSetting("preptime", 0);
        BooleanSetting displayGameTargetsSetting = new BooleanSetting("displaygametargets", true);
        BooleanSetting displayPlayerNamesSetting = new BooleanSetting("displayplayernames", true);
        BooleanSetting displayTimeSetting = new BooleanSetting("displaytime", true);
        StringSetting attackingTeamNameSetting = new StringSetting("attackingteamname", "red");
        StringSetting defendingTeamNameSetting = new StringSetting("defendingteamname", "blue");
        LocationSetting attackingTeamSpawnLocationSetting = new LocationSetting("attackingteamspawnlocation", 0, 0, 0);
        LocationSetting defendingTeamSpawnLocationSetting = new LocationSetting("defendingteamspawnlocation", 0, 0, 0);
        LocationSetting attackingTeamChestLocationSetting = new LocationSetting("attackingteamchestlocation", 0, 0,0);
        LocationSetting defendingTeamChestLocationSetting = new LocationSetting("defendingteamchestlocation", 0, 0,0);
        return new GameManagerSettings(timePerRoundSetting, prepTimeSetting, displayGameTargetsSetting,
                displayPlayerNamesSetting, displayTimeSetting,
                attackingTeamNameSetting, defendingTeamNameSetting,
                attackingTeamSpawnLocationSetting, defendingTeamSpawnLocationSetting,
                attackingTeamChestLocationSetting, defendingTeamChestLocationSetting);
    }

    public void addAllTo(List<Setting> settingsList) {
        settingsList.add(timePerRoundSetting);
        settingsList.add(prepTimeSetting);
        settingsList.add(displayGameTargetsSetting);
        settingsList.add(displayPlayerNamesSetting);
        settingsList.add(displayTimeSetting);
        settingsList.add(attackingTeamNameSetting);
        settingsList.add(defendingTeamNameSetting);
        settingsList.add(attackingTeamSpawnLocationSetting);
        settingsList.add(defendingTeamSpawnLocationSetting);
        settingsList.add(attackingTeamChestLocationSetting);
        settingsList.add(defendingTeamChestLocationSetting);
    }
}
