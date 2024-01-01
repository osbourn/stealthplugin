package me.osbourn.stealthplugin.newsettings;

/**
 * Success or error message when changing a setting from a String
 */
public record SettingChangeResult(boolean wasSuccessful, String message) {
    public static SettingChangeResult success(String message) {
        return new SettingChangeResult(true, message);
    }

    public static SettingChangeResult fail(String message) {
        return new SettingChangeResult(false, message);
    }
}
