package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.newsettings.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SwapRolesCommand implements CommandExecutor {
    public SwapRolesCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("morph.manager")) {
            return false;
        }

        SwapRolesCommand.swapRoles();
        String attackingTeamName = Settings.attackingTeamName;
        String defendingTeamName = Settings.defendingTeamName;
        sender.sendMessage(String.format("%s is now attacking and %s and is now defending",
                attackingTeamName, defendingTeamName));

        return true;
    }

    public static void swapRoles() {
        String temp = Settings.defendingTeamName;
        Settings.defendingTeamName = Settings.attackingTeamName;
        Settings.attackingTeamName = temp;
    }
}
