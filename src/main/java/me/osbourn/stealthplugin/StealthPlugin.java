package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.commands.*;
import me.osbourn.stealthplugin.handlers.*;
import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.Setting;
import me.osbourn.stealthplugin.settingsapi.StringSetting;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class StealthPlugin extends JavaPlugin {
    private List<Setting> settingsList;

    @Override
    public void onEnable() {
        this.settingsList = new ArrayList<>();

        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());
        this.getCommand("randomizeteams").setExecutor(new RandomizeTeamsCommand());
        SettingsCommand settingsCommand = new SettingsCommand(this);
        this.getCommand("settings").setExecutor(settingsCommand);
        this.getCommand("settings").setTabCompleter(settingsCommand);

        MorphManager morphManager = new MorphManager();
        this.getServer().getPluginManager().registerEvents(morphManager, this);
        this.getCommand("morph").setExecutor(new MorphCommand(morphManager));
        this.getCommand("unmorph").setExecutor(new UnmorphCommand(morphManager));

        LocationSetting structurePositionSetting = new LocationSetting("structurepastelocation", 0, 100, 0);
        this.settingsList.add(structurePositionSetting);
        this.getCommand("pastestructure").setExecutor(new PasteStructureCommand(this, structurePositionSetting));

        registerSetting(new KillArrowsHandler());
        registerSetting(new ClearInventoryOnDeathHandler());
        registerSetting(new ProtectLayersHandler());
        registerSetting(new AnnounceBeaconsHandler(morphManager));
        registerSetting(new MorphOnRespawnHandler(morphManager));
        registerSetting(new PlayersDropArrowsHandler(morphManager));

        IntegerSetting timePerRoundSetting = new IntegerSetting("timeperround", 300);
        StringSetting attackingTeamNameSetting = new StringSetting("attackingteamname", "red");
        StringSetting defendingTeamNameSetting = new StringSetting("defendingteamname", "blue");
        LocationSetting attackingTeamSpawnLocationSetting = new LocationSetting("attackingteamspawnpoint", 0, 0, 0);
        LocationSetting defendingTeamSpawnLocationSetting = new LocationSetting("defendingteamspawnpoint", 0, 0, 0);
        this.settingsList.add(timePerRoundSetting);
        this.settingsList.add(attackingTeamNameSetting);
        this.settingsList.add(defendingTeamNameSetting);
        this.settingsList.add(attackingTeamSpawnLocationSetting);
        this.settingsList.add(defendingTeamSpawnLocationSetting);
        GameManager gameManager = new GameManager(this, morphManager, timePerRoundSetting,
                attackingTeamNameSetting, attackingTeamSpawnLocationSetting,
                defendingTeamNameSetting, defendingTeamSpawnLocationSetting);
        this.getServer().getPluginManager().registerEvents(gameManager, this);
        gameManager.runTaskTimer(this, 20, 20);
        this.getCommand("game").setExecutor(new GameCommand(gameManager));
        this.getCommand("togglesb").setExecutor(new ToggleGameScoreboardCommand(gameManager));
    }

    public List<Setting> getSettingsList() {
        return settingsList;
    }

    private void registerSetting(Setting setting) {
        // TODO: Better way of making sure that every setting is a Listener
        settingsList.add(setting);
        if (!(setting instanceof Listener)) {
            throw new AssertionError();
        }
        this.getServer().getPluginManager().registerEvents((Listener) setting, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
