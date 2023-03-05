package me.osbourn.stealthplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class SettingsCommand implements CommandExecutor {
    private final StealthPlugin plugin;
    public SettingsCommand(StealthPlugin plugin) {
        this.plugin = plugin;
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

        Optional<Setting> setting = this.plugin.getSettingsList().stream()
                .filter(s -> s.getName().equals(args[0]))
                .findFirst();

        if (setting.isEmpty()) {
            sender.sendMessage("Unknown setting " + args[0]);
            return false;
        }

        if (args.length == 1) {
            sender.sendMessage(setting.get().getInfo());
        } else {
            String[] passedArgs = Arrays.copyOfRange(args, 1, args.length);
            Optional<String> result = setting.get().trySet(passedArgs);
            result.ifPresent(s -> sender.sendMessage("Error: " + s));
        }
        return true;
    }
}
