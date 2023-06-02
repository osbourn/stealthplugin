package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.commands.*;
import me.osbourn.stealthplugin.handlers.*;
import me.osbourn.stealthplugin.integrations.GlowEffectManager;
import me.osbourn.stealthplugin.integrations.ProtocolIntegration;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.Setting;
import me.osbourn.stealthplugin.util.GameManagerSettings;
import me.osbourn.stealthplugin.util.GameTargets;
import org.bukkit.Bukkit;
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
        BooleanSetting killEntitiesBeforePasteSetting = new BooleanSetting("killentitiesbeforepaste", false);
        this.settingsList.add(structurePositionSetting);
        this.settingsList.add(killEntitiesBeforePasteSetting);
        PasteStructureCommand pasteStructureCommand = new PasteStructureCommand(this, structurePositionSetting, killEntitiesBeforePasteSetting);
        this.getCommand("pastestructure").setExecutor(pasteStructureCommand);

        registerSetting(new KillArrowsHandler());
        registerSetting(new ClearInventoryOnDeathHandler());
        registerSetting(new ProtectLayersHandler());
        registerSetting(new DisableEnderChestsHandler());
        registerSetting(new DisableHungerHandler());
        registerSetting(new PreventRemovingArmorHandler());
        registerSetting(new AnnounceBeaconsHandler(morphManager));
        registerSetting(new BeaconRevealHandler(morphManager));
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

        this.getCommand("game").setExecutor(new GameCommand(gameManager, pasteStructureCommand));
        this.getCommand("gameobjectives").setExecutor(new GameObjectivesCommand(gameManager));
        this.getCommand("revive").setExecutor(new ReviveCommand(gameManager));
        this.getCommand("togglesb").setExecutor(new ToggleGameScoreboardCommand(gameManager));
        this.getCommand("swaproles").setExecutor(new SwapRolesCommand(gameManager));

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolIntegration protocolIntegration = new ProtocolIntegration();
            this.getLogger().info("ProtocolLib specific features enabled");

            BooleanSetting glowingTeammatesSetting = new BooleanSetting("glowingteammates", false);
            this.settingsList.add(glowingTeammatesSetting);
            protocolIntegration.protocolManager.addPacketListener(new GlowEffectManager(this,
                    morphManager, glowingTeammatesSetting));
        } else {
            this.getLogger().warning("ProtocolLib not found, some features will not be available");
        }

        FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();
        for (Setting setting : settingsList) {
            // TODO: Ensure that setting.configValue() is a valid type
            config.addDefault(setting.getName(), setting.configValue());
        }
        this.loadSettings();
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

    public void loadSettings() {
        for (Setting setting : settingsList) {
            // TODO: Ensure that setting.configValue() is a valid type
            setting.setFromConfigValue(this.getConfig().get(setting.getName()));
        }
    }

    public void saveSettings() {
        for (Setting setting : settingsList) {
            this.getConfig().set(setting.getName(), setting.configValue());
        }
        this.saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.saveSettings();
    }
}
