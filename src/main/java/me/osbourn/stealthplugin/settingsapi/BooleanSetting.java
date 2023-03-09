package me.osbourn.stealthplugin.settingsapi;

import me.osbourn.stealthplugin.settingsapi.Setting;

import java.util.List;
import java.util.Optional;

public abstract class BooleanSetting implements Setting {
    private boolean active = true;

    public BooleanSetting(boolean initialValue) {
        this.active = initialValue;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getInfo() {
        return String.format("%s is currently set to %b", this.getName(), this.isActive());
    }

    @Override
    public Optional<String> trySet(String[] arguments) {
        if (arguments.length != 1) {
            return Optional.of("Incorrect number of arguments");
        } else if (arguments[0].equals("true")) {
            this.setActive(true);
            return Optional.empty();
        } else if (arguments[0].equals("false")) {
            this.setActive(false);
            return Optional.empty();
        } else {
            return Optional.of("Expected \"true\" or \"false\"");
        }
    }

    @Override
    public List<String> tabCompletionOptions() {
        return List.of("true", "false");
    }
}
