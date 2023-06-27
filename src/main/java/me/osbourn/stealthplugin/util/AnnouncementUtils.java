package me.osbourn.stealthplugin.util;

import me.osbourn.stealthplugin.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnnouncementUtils {
    private AnnouncementUtils() {
    }

    /**
     * Send a message to all online players
     *
     * @param message The message to send
     */
    public static void announce(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Send a message to all players on the attacking team.
     *
     * @param gameManager The GameManager used to determine who the attacking players are
     * @param message The message to send
     */
    public static void announceToAttackers(GameManager gameManager, String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (gameManager.isOnAttackers(player)) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Send a message to all players on the defending team.
     *
     * @param gameManager The GameManager used to determine who the defending players are
     * @param message The message to send
     */
    public static void announceToDefenders(GameManager gameManager, String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (gameManager.isOnDefenders(player)) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Send a message to all players with the "stealth.manage" permission.
     *
     * @param message The message to send
     */
    public static void announceToAdmins(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("stealth.manage")) {
                player.sendMessage(message);
            }
        }
    }
}
