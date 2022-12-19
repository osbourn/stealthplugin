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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
