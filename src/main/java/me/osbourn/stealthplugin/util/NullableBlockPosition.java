package me.osbourn.stealthplugin.util;

import org.bukkit.Location;

import java.util.Optional;

public record NullableBlockPosition(Optional<BlockPosition> optionalBlockPosition) {
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

    }
}
