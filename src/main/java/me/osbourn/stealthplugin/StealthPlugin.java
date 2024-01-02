package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.commands.*;
import me.osbourn.stealthplugin.handlers.*;
import me.osbourn.stealthplugin.integrations.GlowEffectManager;
import me.osbourn.stealthplugin.integrations.ProtocolIntegration;
import me.osbourn.stealthplugin.newsettings.Settings;
import me.osbourn.stealthplugin.newsettings.SettingsManager;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.settingsapi.IntegerSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import me.osbourn.stealthplugin.settingsapi.Setting;
import me.osbourn.stealthplugin.util.GameLoop;
import me.osbourn.stealthplugin.util.GameTargets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StealthPlugin extends JavaPlugin {

    private List<Setting> settingsList;
    /**
     * Logger for StealthPlugin. Be a bit careful using this early on in plugin initialization
     * since it is a static field set from a non-static method.
     */
    public static Logger LOGGER = null;
    private SettingsManager settingsManager;

    @Override
    public void onEnable() {
        StealthPlugin.LOGGER = this.getLogger();
        this.settingsList = new ArrayList<>();
        this.settingsManager = new SettingsManager(Settings.class, this);
        this.settingsManager.loadSettings();

        this.getCommand("setup").setExecutor(new SetupCommand());
        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());
        this.getCommand("randomizeteams").setExecutor(new RandomizeTeamsCommand());
        this.getCommand("swapteams").setExecutor(new SwapTeamsCommand());
        SettingsCommand settingsCommand = new SettingsCommand(this.settingsManager);
        this.getCommand("settings").setExecutor(settingsCommand);
        this.getCommand("settings").setTabCompleter(settingsCommand);

        BooleanSetting morphedPlayersCanAttackSetting = new BooleanSetting("morphedplayerscanattack", false);
        BooleanSetting morphedPlayersIgnoreArrowsSetting = new BooleanSetting("morphedplayersignorearrows", true);
        this.settingsList.add(morphedPlayersCanAttackSetting);
        this.settingsList.add(morphedPlayersIgnoreArrowsSetting);
        MorphManager morphManager = new MorphManager(morphedPlayersCanAttackSetting, morphedPlayersIgnoreArrowsSetting);
        this.getServer().getPluginManager().registerEvents(morphManager, this);
        this.getCommand("morph").setExecutor(new MorphCommand(morphManager));
        this.getCommand("unmorph").setExecutor(new UnmorphCommand(morphManager));

        LocationSetting structurePositionSetting = new LocationSetting("structurepastelocation", 0, 100, 0);
        BooleanSetting killEntitiesBeforePasteSetting = new BooleanSetting("killentitiesbeforepaste", true);
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
        registerSetting(new IncreaseEnvironmentalDamageHandler());
        registerSetting(new AnnounceBeaconsHandler(morphManager));
        registerSetting(new BeaconRevealHandler(morphManager));
        registerSetting(new MorphOnRespawnHandler(morphManager));
        registerSetting(new PlayersDropArrowsHandler(morphManager));

        GameTargets gameTargets = new GameTargets();
        registerSetting(new GameTargetsHandler(gameTargets, morphManager));

        ScoreManager scoreManager = new ScoreManager();

        KitManager kitManager = new KitManager();
        this.getServer().getPluginManager().registerEvents(kitManager, this);

        GameManager gameManager = new GameManager(this, morphManager, scoreManager, kitManager, gameTargets);
        this.getServer().getPluginManager().registerEvents(gameManager, this);
        gameManager.runTaskTimer(this, 20, 20);

        IntegerSetting timeInLobbySetting = new IntegerSetting("timeinlobby", 30);
        GameLoop gameLoop = new GameLoop(this, gameManager, pasteStructureCommand, timeInLobbySetting);
        gameManager.setRunAfterGame(gameLoop::runRunnableIfActive);

        registerSetting(new PrepTimeHandler(gameManager));
        registerSetting(new PreventPrematureTargetDestructionHandler(gameManager));

        this.getCommand("game").setExecutor(new GameCommand(gameManager, pasteStructureCommand, gameLoop));
        this.getCommand("gameobjectives").setExecutor(new GameObjectivesCommand(gameManager));
        this.getCommand("revive").setExecutor(new ReviveCommand(gameManager));
        this.getCommand("togglesb").setExecutor(new ToggleGameScoreboardCommand(gameManager));
        this.getCommand("selecttarget").setExecutor(new SelectTargetsCommand(gameManager));
        this.getCommand("swaproles").setExecutor(new SwapRolesCommand(gameManager));
        this.getCommand("score").setExecutor(new ScoreCommand(scoreManager));

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolIntegration protocolIntegration = new ProtocolIntegration();
            this.getLogger().info("ProtocolLib specific features enabled");

            BooleanSetting glowingTeammatesSetting = new BooleanSetting("glowingteammates", false);
            this.settingsList.add(glowingTeammatesSetting);
            GlowEffectManager glowEffectManager = new GlowEffectManager(this, morphManager, glowingTeammatesSetting);

            protocolIntegration.setup(glowEffectManager);
        } else {
            this.getLogger().warning("ProtocolLib not found, some features will not be available");
        }

        FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();
        for (Setting setting : settingsList) {
            // TODO: Ensure that setting.configValue() is a valid type
            config.addDefault(setting.getName(), setting.configValue());
        }
        //this.loadSettings();
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
        this.settingsManager.saveSettings();
    }
}
