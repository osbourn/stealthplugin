package me.osbourn.stealthplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillArrowsHandler implements Listener, CommandExecutor {
    private boolean isActive = true;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (this.isActive() && event.getDamager().getType() == EntityType.ARROW) {
            event.setDamage(1000);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.togglekillarrows")) {
            return false;
        }

        this.setActive(!this.isActive());
        if (this.isActive()) {
            sender.sendMessage("Arrow insta-kills enabled");
        } else {
            sender.sendMessage("Arrow insta-kills disabled");
        }
        return true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean state) {
        this.isActive = state;
    }
}
