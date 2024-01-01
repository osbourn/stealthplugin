package me.osbourn.stealthplugin.newsettings;

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
}
