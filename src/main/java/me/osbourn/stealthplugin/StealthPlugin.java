package me.osbourn.stealthplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class StealthPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        KillArrowsHandler killArrowsHandler = new KillArrowsHandler();
        this.getServer().getPluginManager().registerEvents(killArrowsHandler, this);
        this.getCommand("togglekillarrows").setExecutor(killArrowsHandler);

        AnnounceBeaconsHandler announceBeaconsHandler = new AnnounceBeaconsHandler();
        this.getServer().getPluginManager().registerEvents(announceBeaconsHandler, this);
        this.getCommand("toggleannouncebeacons").setExecutor(announceBeaconsHandler);

        ProtectLayersHandler protectLayersHandler = new ProtectLayersHandler();
        this.getServer().getPluginManager().registerEvents(protectLayersHandler, this);
        this.getCommand("setprotectedlayer").setExecutor(protectLayersHandler);

        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());

        MorphManager morphManager = new MorphManager();
        this.getServer().getPluginManager().registerEvents(morphManager, this);
        this.getCommand("morph").setExecutor(new MorphCommand(morphManager));
        this.getCommand("unmorph").setExecutor(new UnmorphCommand(morphManager));

        MorphOnRespawnHandler morphOnRespawnHandler = new MorphOnRespawnHandler(morphManager);
        this.getServer().getPluginManager().registerEvents(morphOnRespawnHandler, this);
        this.getCommand("togglemorphonrespawn").setExecutor(morphOnRespawnHandler);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
