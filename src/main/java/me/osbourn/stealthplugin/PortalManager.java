package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.settings.Settings;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class PortalManager implements Listener {
    private final List<Location> portalLocations;

    public PortalManager() {
        this.portalLocations = new ArrayList<>();
    }

    @EventHandler
    public void createPortal(BlockPlaceEvent event) {
        if (Settings.portalSystemEnabled && event.getBlockPlaced().getType() == Material.END_PORTAL_FRAME) {
            Location loc = event.getBlockPlaced().getLocation().clone();

            // Should be unnecessary but just in case
            loc.setWorld(event.getBlockPlaced().getWorld());

            for (var existingPortalLocation : portalLocations) {
                if (existingPortalLocation.distance(loc) < 3.0) {
                    event.getPlayer().sendMessage("You can't place portals too close to existing portals!");
                    event.setCancelled(true);
                    return;
                }
            }

            portalLocations.add(loc);
        }
    }

    @EventHandler
    public void sneakOnPortal(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (Settings.portalSystemEnabled && event.isSneaking() && canPlayerTeleport(player)) {
            Location loc = player.getLocation();
            // .clone().subtract(0.0, 1.0, 0.0) is not needed because portals are not a full block
            if (loc.getBlock().getType() == Material.END_PORTAL_FRAME) {
                OptionalInt portalIndex = IntStream.range(0, this.portalLocations.size())
                        .filter(i -> locationsHaveEqualBlockCoordinates(portalLocations.get(i), loc)
                                && Objects.equals(portalLocations.get(i).getWorld(), player.getWorld()))
                        .findFirst();
                if (portalIndex.isPresent()) {
                    int newPortalIndex = (portalIndex.getAsInt() + 1) % portalLocations.size();
                    float yawBeforeTeleport = player.getLocation().getYaw();
                    float pitchBeforeTeleport = player.getLocation().getPitch();
                    // 0.5 is to end up in the middle of the block rather than on the corner, and end portals are 13/16 blocks tall
                    Location destination = this.portalLocations.get(newPortalIndex).clone().add(0.5, 13.0 / 16.0, 0.5);
                    destination.setYaw(yawBeforeTeleport);
                    destination.setPitch(pitchBeforeTeleport);
                    player.teleport(destination);
                }
            }
        }
    }

    private static boolean locationsHaveEqualBlockCoordinates(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    private static boolean canPlayerTeleport(Player player) {
        return player.getGameMode() == GameMode.CREATIVE || GameManager.isOnDefenders(player);
    }

    public void clearPortals() {
        this.portalLocations.clear();
    }
}
