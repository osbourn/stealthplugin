package me.osbourn.stealthplugin.settingsapi;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;

public class LocationSetting implements Setting {
    private final String settingName;
    private int x;
    private int y;
    private int z;

    public LocationSetting(String settingName, int initialX, int initialY, int initialZ) {
        this.settingName = settingName;
        this.x = initialX;
        this.y = initialY;
        this.z = initialZ;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int z() {
        return this.z;
    }

    public Location toLocationWithNullWorld() {
        return new Location(null, this.x(), this.y(), this.z());
    }

    public Location toLocationInWorld(World world) {
        return new Location(world, this.x(), this.y(), this.z());
    }

    @Override
    public String getName() {
        return this.settingName;
    }

    @Override
    public String valueAsString() {
        return String.format("%d %d %d", this.x(), this.y(), this.z());
    }

    @Override
    public Optional<String> trySet(String[] arguments) {
        if (arguments.length != 3) {
            return Optional.of("Expected 3 arguments");
        }
        try {
            this.x = Integer.parseInt(arguments[0]);
            this.y = Integer.parseInt(arguments[1]);
            this.z = Integer.parseInt(arguments[2]);
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.of("Invalid number");
        }
    }

    @Override
    public Object configValue() {
        return this.toLocationWithNullWorld();
    }

    @Override
    public void setFromConfigValue(Object value) {
        if (value instanceof Location loc) {
            this.x = (int) loc.getX();
            this.y = (int) loc.getY();
            this.z = (int) loc.getZ();
        }
    }
}
