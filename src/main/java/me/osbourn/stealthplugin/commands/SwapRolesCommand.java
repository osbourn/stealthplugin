package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SwapRolesCommand implements CommandExecutor {
    private final GameManager gameManager;

    public SwapRolesCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("morph.manager")) {
            return false;
        }

        SwapRolesCommand.swapRoles(this.gameManager);
        String attackingTeamName = this.gameManager.getSettings().attackingTeamNameSetting().getValue();
        String defendingTeamName = this.gameManager.getSettings().defendingTeamNameSetting().getValue();
        sender.sendMessage(String.format("%s is now attacking and %s and is now defending",
                attackingTeamName, defendingTeamName));

        return true;
    }

    public static void swapRoles(GameManager gameManager) {
        String attackingTeamName = gameManager.getSettings().attackingTeamNameSetting().getValue();
        String defendingTeamName = gameManager.getSettings().defendingTeamNameSetting().getValue();
        gameManager.getSettings().attackingTeamNameSetting().setValue(defendingTeamName);
        gameManager.getSettings().defendingTeamNameSetting().setValue(attackingTeamName);
    }
}
