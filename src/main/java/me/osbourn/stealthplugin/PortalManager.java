package me.osbourn.stealthplugin;

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
    private List<Location> portalLocations;

    public PortalManager() {
        this.portalLocations = new ArrayList<>();
    }

    @EventHandler
    public void createPortal(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.END_PORTAL_FRAME) {
            Location loc = event.getBlockPlaced().getLocation().clone();
            loc.setWorld(event.getBlockPlaced().getWorld());
            portalLocations.add(loc);
        }
    }

    @EventHandler
    public void sneakOnPortal(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()) {
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
}
