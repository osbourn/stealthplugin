package me.osbourn.stealthplugin.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;

public record NullableBlockPosition(Optional<BlockPosition> optionalBlockPosition) {
    public static final NullableBlockPosition UNSET = new NullableBlockPosition(Optional.empty());

    public static NullableBlockPosition fromCoords(int x, int y, int z) {
        return new NullableBlockPosition(Optional.of(new BlockPosition(x, y, z)));
    }

    public boolean isSet() {
        return optionalBlockPosition.isPresent();
    }

    public BlockPosition toBlockPosition() {
        return optionalBlockPosition.get();
    }

    public int x() {
        return toBlockPosition().x();
    }

    public int y() {
        return toBlockPosition().y();
    }

    public int z() {
        return toBlockPosition().z();
    }

    public Location toLocationWithNullWorld() {
        return new Location(null, this.x(), this.y(), this.z());
    }

    public Location toLocationInWorld(World world) {
        return new Location(world, this.x(), this.y(), this.z());
    }

    /**
     * Returns a String like "100 64 -100" or "unset"
     */
    public String toStringWithSpaces() {
        if (this.isSet()) {
            return String.format("%d %d %d", this.x(), this.y(), this.z());
        } else {
            return "unset";
        }
    }
}
