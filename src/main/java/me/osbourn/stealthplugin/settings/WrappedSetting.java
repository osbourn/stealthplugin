package me.osbourn.stealthplugin.settings;

import org.jetbrains.annotations.Nullable;

public interface WrappedSetting {
    String getName();
    SettingChangeResult setFromString(String s);
    String valueAsString();
    default String infoMessage() {
        return String.format("\"%s\" is currently set to \"%s\"", this.getName(), this.valueAsString());
    }
    Object toConfigValue();
    void setFromConfigValue(@Nullable Object value);

    default String[] tabCompletionOptions(String[] currentArgs) {
        return new String[0];
    }
}
