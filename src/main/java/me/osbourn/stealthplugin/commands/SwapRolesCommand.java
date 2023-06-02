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

        String attackingTeamName = this.gameManager.getSettings().attackingTeamNameSetting().getValue();
        String defendingTeamName = this.gameManager.getSettings().defendingTeamNameSetting().getValue();
        this.gameManager.getSettings().attackingTeamNameSetting().setValue(defendingTeamName);
        this.gameManager.getSettings().defendingTeamNameSetting().setValue(attackingTeamName);
        sender.sendMessage(String.format("Changed %s from attackers to defenders and %s from defenders to attackers",
                attackingTeamName, defendingTeamName));

        return true;
    }
}
