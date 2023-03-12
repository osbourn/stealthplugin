package me.osbourn.stealthplugin.settingsapi;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class StringSetting implements Setting {
    private final String settingName;
    @Nullable
    public Function<String, Boolean> validator;
    private String value;

    public StringSetting(String settingName, String initialValue) {
        this(settingName, initialValue, null);
    }

    public StringSetting(String settingName, String initialValue, @Nullable Function<String, Boolean> validator) {
        this.settingName = settingName;
        this.value = initialValue;
        this.validator = validator;
    }

    @Override
    public String getName() {
        return this.settingName;
    }

    @Override
    public String getInfo() {
        return String.format("%s is currently set to %s", this.getName(), this.value);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public Optional<String> trySet(String[] arguments) {
        // TODO: Support strings with spaces
        if (arguments.length != 1) {
            return Optional.of("Incorrect number of arguments");
        }
        if (validator == null) {
            this.value = arguments[0];
            return Optional.empty();
        } else {
            boolean isValid = this.validator.apply(arguments[0]);
            if (isValid) {
                this.value = arguments[0];
                return Optional.empty();
            } else {
                return Optional.of("Invalid String");
            }
        }
    }
}
