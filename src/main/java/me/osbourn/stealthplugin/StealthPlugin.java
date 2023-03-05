package me.osbourn.stealthplugin;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class StealthPlugin extends JavaPlugin {
    private List<Setting> settingsList;

    @Override
    public void onEnable() {
        this.settingsList = new ArrayList<>();

        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());
        this.getCommand("randomizeteams").setExecutor(new RandomizeTeamsCommand());
        this.getCommand("settings").setExecutor(new SettingsCommand(this));

        MorphManager morphManager = new MorphManager();
        this.getServer().getPluginManager().registerEvents(morphManager, this);
        this.getCommand("morph").setExecutor(new MorphCommand(morphManager));
        this.getCommand("unmorph").setExecutor(new UnmorphCommand(morphManager));

        TogglableHandler.registerHandler(new KillArrowsHandler(), "togglekillarrows", this);
        TogglableHandler.registerHandler(new ClearInventoryOnDeathHandler(), "toggleclearinventoryondeath", this);
        TogglableHandler.registerHandler(new ProtectLayersHandler(), "setprotectedlayer", this);
        TogglableHandler.registerHandler(new AnnounceBeaconsHandler(morphManager), "toggleannouncebeacons", this);
        TogglableHandler.registerHandler(new MorphOnRespawnHandler(morphManager), "togglemorphonrespawn", this);
        TogglableHandler.registerHandler(new PlayersDropArrowsHandler(morphManager), "toggleplayersdroparrows", this);
    }

    public List<Setting> getSettingsList() {
        return settingsList;
    }

    private void registerSetting(Setting setting) {
        settingsList.add(setting);
        if (setting instanceof Listener l) {
            this.getServer().getPluginManager().registerEvents(l, this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
