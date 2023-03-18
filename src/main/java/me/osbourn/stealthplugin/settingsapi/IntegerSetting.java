package me.osbourn.stealthplugin.settingsapi;

import java.util.Optional;

public class IntegerSetting implements Setting {
    private final String settingName;
    private int value;

    public IntegerSetting(String settingName, int initialValue) {
        this.settingName = settingName;
        this.value = initialValue;
    }

    @Override
    public final String getName() {
        return this.settingName;
    }

    @Override
    public final String getInfo() {
        return String.format("%s is currently set to %d", this.getName(), this.value);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public final Optional<String> trySet(String[] arguments) {
        if (arguments.length != 1) {
            return Optional.of("Incorrect number of arguments");
        }
        try {
            this.setValue(Integer.parseInt(arguments[0]));
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.of("Invalid number");
        }
    }

    @Override
    public Object configValue() {
        return this.value;
    }

    @Override
    public void setFromConfigValue(Object value) {
        if (value instanceof Integer) {
            this.value = (Integer) value;
        }
    }
}
