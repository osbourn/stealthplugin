package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.commands.*;
import me.osbourn.stealthplugin.handlers.*;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.Setting;
import me.osbourn.stealthplugin.util.GameManagerSettings;
import me.osbourn.stealthplugin.util.GameTargets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class StealthPlugin extends JavaPlugin {
    private List<Setting> settingsList;

    @Override
    public void onEnable() {
        this.settingsList = new ArrayList<>();

        this.getCommand("setup").setExecutor(new SetupCommand());
        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());
        this.getCommand("randomizeteams").setExecutor(new RandomizeTeamsCommand());
        this.getCommand("swapteams").setExecutor(new SwapTeamsCommand());
        SettingsCommand settingsCommand = new SettingsCommand(this);
        this.getCommand("settings").setExecutor(settingsCommand);
        this.getCommand("settings").setTabCompleter(settingsCommand);

        BooleanSetting morphedPlayersCanAttackSetting = new BooleanSetting("morphedplayerscanattack", true);
        BooleanSetting morphedPlayersIgnoreArrowsSetting = new BooleanSetting("morphedplayersignorearrows", true);
        this.settingsList.add(morphedPlayersCanAttackSetting);
        this.settingsList.add(morphedPlayersIgnoreArrowsSetting);
        MorphManager morphManager = new MorphManager(morphedPlayersCanAttackSetting, morphedPlayersIgnoreArrowsSetting);
        this.getServer().getPluginManager().registerEvents(morphManager, this);
        this.getCommand("morph").setExecutor(new MorphCommand(morphManager));
        this.getCommand("unmorph").setExecutor(new UnmorphCommand(morphManager));

        LocationSetting structurePositionSetting = new LocationSetting("structurepastelocation", 0, 100, 0);
        this.settingsList.add(structurePositionSetting);
        this.getCommand("pastestructure").setExecutor(new PasteStructureCommand(this, structurePositionSetting));

        registerSetting(new KillArrowsHandler());
        registerSetting(new ClearInventoryOnDeathHandler());
        registerSetting(new ProtectLayersHandler());
        registerSetting(new DisableEnderChestsHandler());
        registerSetting(new DisableHungerHandler());
        registerSetting(new AnnounceBeaconsHandler(morphManager));
        registerSetting(new MorphOnRespawnHandler(morphManager));
        registerSetting(new PlayersDropArrowsHandler(morphManager));

        GameTargets gameTargets = new GameTargets();
        registerSetting(new GameTargetsHandler(gameTargets, morphManager));

        GameManagerSettings gameManagerSettings = GameManagerSettings.makeNew();
        gameManagerSettings.addAllTo(this.settingsList);
        GameManager gameManager = new GameManager(this, morphManager, gameTargets, gameManagerSettings);
        this.getServer().getPluginManager().registerEvents(gameManager, this);
        gameManager.runTaskTimer(this, 20, 20);

        registerSetting(new PrepTimeHandler(gameManager));

        this.getCommand("game").setExecutor(new GameCommand(gameManager));
        this.getCommand("togglesb").setExecutor(new ToggleGameScoreboardCommand(gameManager));

        FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();
        for (Setting setting : settingsList) {
            // TODO: Ensure that setting.configValue() is a valid type
            config.set(setting.getName(), setting.configValue());
        }
        this.saveConfig();
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
