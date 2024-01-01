package me.osbourn.stealthplugin.newsettings;

public interface WrappedSetting {
    String getName();
    void setFromString();
    Object toConfigValue();
    void setFromConfigValue();
}
