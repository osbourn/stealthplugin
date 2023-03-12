package me.osbourn.stealthplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Team;

public class GiveTeamArmorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        giveTeamArmor();
        return true;
    }

    public static void giveTeamArmor() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (team != null) {
                Color color = chatColorToColor(team.getColor());
                if (color != null) {
                    player.getInventory().setHelmet(createDyedLeatherArmor(Material.LEATHER_HELMET, color));
                    player.getInventory().setChestplate(createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, color));
                    player.getInventory().setLeggings(createDyedLeatherArmor(Material.LEATHER_LEGGINGS, color));
                    player.getInventory().setBoots(createDyedLeatherArmor(Material.LEATHER_BOOTS, color));
                }
            }
        }
    }

    private static ItemStack createDyedLeatherArmor(Material leatherArmorType, Color color) {
        ItemStack itemStack = new ItemStack(leatherArmorType);
        LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
        meta.setColor(color);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private static Color chatColorToColor(ChatColor color) {
        return switch (color) {
            case BLACK -> Color.BLACK;
            case DARK_BLUE, BLUE -> Color.BLUE;
            case DARK_GREEN, GREEN -> Color.GREEN;
            case DARK_AQUA, AQUA -> Color.AQUA;
            case DARK_RED, RED -> Color.RED;
            case DARK_PURPLE, LIGHT_PURPLE -> Color.PURPLE;
            case GOLD, YELLOW -> Color.YELLOW;
            case GRAY, DARK_GRAY -> Color.GRAY;
            case WHITE -> Color.WHITE;
            default -> null;
        };
    }
}
