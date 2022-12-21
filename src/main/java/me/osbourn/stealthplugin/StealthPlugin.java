package me.osbourn.stealthplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class StealthPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("giveteamarmor").setExecutor(new GiveTeamArmorCommand());

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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
