package me.osbourn.stealthplugin.newsettings;

/**
 * Success or error message when changing a setting from a String
 */
public record SettingChangeResult(boolean wasSuccessful, String message) {
}
