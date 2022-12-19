package me.osbourn.stealthplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class StealthPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        KillArrowsHandler killArrowsHandler = new KillArrowsHandler();
        this.getServer().getPluginManager().registerEvents(killArrowsHandler, this);
        this.getCommand("togglekillarrows").setExecutor(killArrowsHandler);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
