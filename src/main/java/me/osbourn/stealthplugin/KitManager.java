package me.osbourn.stealthplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitManager implements Listener {
    /**
     * Associates the UUID of each player to the location of the chest that kit items will be pulled from when the
     * player is on the attacking team.
     */
    private final Map<UUID, Location> attackerKitLocations;
    /**
     * Associates the UUID of each player to the location of the chest that kit items will be pulled from when the
     * player is on the defending team.
     */
    private final Map<UUID, Location> defenderKitLocations;

    public KitManager() {
        this.attackerKitLocations = new HashMap<>();
        this.defenderKitLocations = new HashMap<>();
    }

    public void setAttackingKitLocation(Player player, Location location) {
        this.attackerKitLocations.put(player.getUniqueId(), location);
    }

    public @Nullable Location getAttackingKitLocation(Player player) {
        return this.attackerKitLocations.get(player.getUniqueId());
    }

    public void setDefendingKitLocation(Player player, Location location) {
        this.defenderKitLocations.put(player.getUniqueId(), location);
    }

    public @Nullable Location getDefendingKitLocation(Player player) {
        return this.defenderKitLocations.get(player.getUniqueId());
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getGameMode() != GameMode.SPECTATOR) {
            assert event.getClickedBlock() != null;
            if (Tag.WALL_SIGNS.isTagged(event.getClickedBlock().getType())) {
                WallSign sign = (WallSign) event.getClickedBlock().getBlockData();
                // If the sign is facing South (toward positive Z), then the chest will be 2 blocks in the negative Z
                // direction and the block the sign is mounted on will be 1 block in the negative Z direction
                Location anchorLoc = event.getClickedBlock().getLocation()
                        .add(-1 * sign.getFacing().getModX(), 0, -1 * sign.getFacing().getModZ());
                Location chestLoc = event.getClickedBlock().getLocation()
                        .add(-2 * sign.getFacing().getModX(), 0, -2 * sign.getFacing().getModZ());
                if (chestLoc.getBlock().getType() == Material.CHEST) {
                    if (anchorLoc.getBlock().getType() == Material.GOLD_BLOCK) {
                        this.setAttackingKitLocation(player, chestLoc);
                        player.sendMessage(ChatColor.AQUA + "Selected attacking kit");
                    } else if (anchorLoc.getBlock().getType() == Material.IRON_BLOCK) {
                        this.setDefendingKitLocation(player, chestLoc);
                        player.sendMessage(ChatColor.AQUA + "Selected defending kit");
                    }
                }
            }
        }
    }

    public void givePlayerAttackingKit(Player player) {
        Location chestLoc = this.getAttackingKitLocation(player);
        if (chestLoc != null) {
            givePlayerItemsInChest(chestLoc, player);
        } else {
            player.sendMessage("No kit selected!");
        }
    }

    public void givePlayerDefendingKit(Player player) {
        Location chestLoc = this.getDefendingKitLocation(player);
        if (chestLoc != null) {
            givePlayerItemsInChest(chestLoc, player);
        } else {
            player.sendMessage("No kit selected!");
        }
    }

    private void givePlayerItemsInChest(Location chestLocation, Player player) {
        if (chestLocation.getBlock().getType() != Material.CHEST) {
            player.sendMessage("The selected kit was not a chest (please contact admin)");
            return;
        }

        Block block = chestLocation.getBlock();
        BlockState blockState = block.getState();
        if (!(blockState instanceof Chest chest)) {
            player.sendMessage("Failed to give starting items, because chest doesn't have chest data (please contact admin)");
            return;
        }

        Inventory inventory = chest.getBlockInventory();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                player.getInventory().addItem(itemStack.clone());
            }
        }
    }
}
