package me.osbourn.stealthplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class StealthPlugin extends JavaPlugin {
    @Override
    public void onEnable() {

        TogglableHandler.registerHandler(new KillArrowsHandler(), "togglekillarrows", this);
        TogglableHandler.registerHandler(new AnnounceBeaconsHandler(), "toggleannouncebeacons", this);

        ProtectLayersHandler protectLayersHandler = new ProtectLayersHandler();
        this.getServer().getPluginManager().registerEvents(protectLayersHandler, this);
        this.getCommand("setprotectedlayer").setExecutor(protectLayersHandler);

        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());

        MorphManager morphManager = new MorphManager();
        this.getServer().getPluginManager().registerEvents(morphManager, this);
        this.getCommand("morph").setExecutor(new MorphCommand(morphManager));
        this.getCommand("unmorph").setExecutor(new UnmorphCommand(morphManager));

        TogglableHandler.registerHandler(new MorphOnRespawnHandler(morphManager), "togglemorphonrespawn", this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
