package me.osbourn.stealthplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TogglableHandler implements Listener, CommandExecutor {
    private boolean isActive = true;

    /**
     * Get a few word description of the setting, used for command output.
     * For example, "Beacon destruction announcements".
     */
    protected abstract String description();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        this.setActive(!this.isActive());
        if (this.isActive()) {
            sender.sendMessage(description() + " enabled");
        } else {
            sender.sendMessage(description() + " disabled");
        }
        return true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean state) {
        this.isActive = state;
    }

    public static void registerHandler(TogglableHandler handler, String command, JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(handler, plugin);
        plugin.getCommand(command).setExecutor(handler);
    }
}
