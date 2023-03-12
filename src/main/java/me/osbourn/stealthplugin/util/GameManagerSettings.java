package me.osbourn.stealthplugin.util;

import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.Setting;
import me.osbourn.stealthplugin.settingsapi.StringSetting;

import java.util.List;

public record GameManagerSettings(IntegerSetting timePerRoundSetting,
                                  StringSetting attackingTeamNameSetting, StringSetting defendingTeamNameSetting,
                                  LocationSetting attackingTeamSpawnLocationSetting, LocationSetting defendingTeamSpawnLocationSetting,
                                  LocationSetting attackingTeamChestLocationSetting, LocationSetting defendingTeamChestLocationSetting) {

    public static GameManagerSettings makeNew() {
        IntegerSetting timePerRoundSetting = new IntegerSetting("timeperround", 300);
        StringSetting attackingTeamNameSetting = new StringSetting("attackingteamname", "red");
        StringSetting defendingTeamNameSetting = new StringSetting("defendingteamname", "blue");
        LocationSetting attackingTeamSpawnLocationSetting = new LocationSetting("attackingteamspawnlocation", 0, 0, 0);
        LocationSetting defendingTeamSpawnLocationSetting = new LocationSetting("defendingteamspawnlocation", 0, 0, 0);
        LocationSetting attackingTeamChestLocationSetting = new LocationSetting("attackingteamchestlocation", 0, 0,0);
        LocationSetting defendingTeamChestLocationSetting = new LocationSetting("defendingteamchestlocation", 0, 0,0);
        return new GameManagerSettings(timePerRoundSetting, attackingTeamNameSetting, defendingTeamNameSetting,
                attackingTeamSpawnLocationSetting, defendingTeamSpawnLocationSetting,
                attackingTeamChestLocationSetting, defendingTeamChestLocationSetting);
    }

    public void addAllTo(List<Setting> settingsList) {
        settingsList.add(timePerRoundSetting);
        settingsList.add(attackingTeamNameSetting);
        settingsList.add(defendingTeamNameSetting);
        settingsList.add(attackingTeamSpawnLocationSetting);
        settingsList.add(defendingTeamSpawnLocationSetting);
        settingsList.add(attackingTeamChestLocationSetting);
        settingsList.add(defendingTeamChestLocationSetting);
    }
}
