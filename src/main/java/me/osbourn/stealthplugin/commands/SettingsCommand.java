package me.osbourn.stealthplugin.commands;

import me.osbourn.stealthplugin.settings.SettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SettingsCommand implements CommandExecutor, TabCompleter {
    private final SettingsManager settingsManager;

    public SettingsCommand(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Too few arguments");
            return false;
        }

        if (args[0].equals("save")) {
            settingsManager.saveSettings();
            sender.sendMessage("Saved settings to config");
            return true;
        }
        if (args[0].equals("load")) {
            settingsManager.loadSettings();
            sender.sendMessage("Loaded settings from config");
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(settingsManager.getInfoMessage(args[0]));
            return true;
        } else {
            String[] passedArgs = Arrays.copyOfRange(args, 1, args.length);
            String valueToSet = String.join(" ", passedArgs);
            var result = settingsManager.changeSetting(args[0], valueToSet);
            sender.sendMessage(result.message());
            return result.wasSuccessful();
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                      @NotNull String[] args) {
        if (args.length == 0) {
            return this.settingsManager.getSettingNames();
        } else if (args.length == 1) {
            return this.settingsManager.getSettingNames().stream().filter(s -> s.startsWith(args[0])).toList();
        } else {
            // We cut off the first and last index in args because the first index is the setting name
            // and the last index is what is currently being entered
            String[] currentArgs = Arrays.copyOfRange(args, 1, args.length - 1);
            return Arrays.stream(this.settingsManager.getTabCompletionOptions(args[0], currentArgs))
                    .filter(s -> s.startsWith(args[args.length - 1]))
                    .toList();
        }
    }
}
