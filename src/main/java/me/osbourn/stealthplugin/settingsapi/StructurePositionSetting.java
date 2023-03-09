package me.osbourn.stealthplugin.settingsapi;

import me.osbourn.stealthplugin.settingsapi.Setting;

import java.util.Optional;

public class StructurePositionSetting implements Setting {
    private int x = 0;
    private int y = 100;
    private int z = 0;

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int z() {
        return this.z;
    }

    @Override
    public String getName() {
        return "structurepastelocation";
    }

    @Override
    public String getInfo() {
        return String.format("structurepastelocation is currently set to %d %d %d", this.x(), this.y(), this.z());
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
}
