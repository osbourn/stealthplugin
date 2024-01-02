package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.commands.*;
import me.osbourn.stealthplugin.handlers.*;
import me.osbourn.stealthplugin.integrations.GlowEffectManager;
import me.osbourn.stealthplugin.integrations.ProtocolIntegration;
import me.osbourn.stealthplugin.settings.Settings;
import me.osbourn.stealthplugin.settings.SettingsManager;
import me.osbourn.stealthplugin.util.GameLoop;
import me.osbourn.stealthplugin.util.GameTargets;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class StealthPlugin extends JavaPlugin {
    /**
     * Logger for StealthPlugin. Be a bit careful using this early on in plugin initialization
     * since it is a static field set from a non-static method.
     */
    public static Logger LOGGER = null;
    private SettingsManager settingsManager;

    @Override
    public void onEnable() {
        StealthPlugin.LOGGER = this.getLogger();
        this.settingsManager = new SettingsManager(Settings.class, this);
        this.settingsManager.loadSettings();

        this.getCommand("setup").setExecutor(new SetupCommand());
        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());
        this.getCommand("randomizeteams").setExecutor(new RandomizeTeamsCommand());
        this.getCommand("swapteams").setExecutor(new SwapTeamsCommand());
        SettingsCommand settingsCommand = new SettingsCommand(this.settingsManager);
        this.getCommand("settings").setExecutor(settingsCommand);
        this.getCommand("settings").setTabCompleter(settingsCommand);

        MorphManager morphManager = new MorphManager();
        registerListener(morphManager);
        this.getCommand("unmorph").setExecutor(new UnmorphCommand(morphManager));

        PasteStructureCommand pasteStructureCommand = new PasteStructureCommand(this);
        this.getCommand("pastestructure").setExecutor(pasteStructureCommand);

        registerListener(new KillArrowsHandler());
        registerListener(new ClearInventoryOnDeathHandler());
        registerListener(new ProtectLayersHandler());
        registerListener(new DisableEnderChestsHandler());
        registerListener(new DisableHungerHandler());
        registerListener(new PreventRemovingArmorHandler());
        registerListener(new IncreaseEnvironmentalDamageHandler());
        registerListener(new AnnounceBeaconsHandler(morphManager));
        registerListener(new BeaconRevealHandler(morphManager));
        registerListener(new MorphOnRespawnHandler(morphManager));
        registerListener(new PlayersDropArrowsHandler(morphManager));

        GameTargets gameTargets = new GameTargets();
        registerListener(new GameTargetsHandler(gameTargets, morphManager));

        ScoreManager scoreManager = new ScoreManager();

        KitManager kitManager = new KitManager();
        registerListener(morphManager);

        GameManager gameManager = new GameManager(this, morphManager, scoreManager, kitManager, gameTargets);
        registerListener(gameManager);
        gameManager.runTaskTimer(this, 20, 20);

        GameLoop gameLoop = new GameLoop(this, gameManager, pasteStructureCommand);
        gameManager.setRunAfterGame(gameLoop::runRunnableIfActive);

        registerListener(new PrepTimeHandler(gameManager));
        registerListener(new PreventPrematureTargetDestructionHandler(gameManager));

        this.getCommand("game").setExecutor(new GameCommand(gameManager, pasteStructureCommand, gameLoop));
        this.getCommand("gameobjectives").setExecutor(new GameObjectivesCommand(gameManager));
        this.getCommand("revive").setExecutor(new ReviveCommand(gameManager));
        this.getCommand("togglesb").setExecutor(new ToggleGameScoreboardCommand(gameManager));
        this.getCommand("selecttarget").setExecutor(new SelectTargetsCommand(gameManager));
        this.getCommand("swaproles").setExecutor(new SwapRolesCommand());
        this.getCommand("score").setExecutor(new ScoreCommand(scoreManager));

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolIntegration protocolIntegration = new ProtocolIntegration();
            this.getLogger().info("ProtocolLib specific features enabled");

            GlowEffectManager glowEffectManager = new GlowEffectManager(this, morphManager);

            protocolIntegration.setup(glowEffectManager);
        } else {
            this.getLogger().warning("ProtocolLib not found, some features will not be available");
        }
    }

    private void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        this.settingsManager.saveSettings();
    }
}
