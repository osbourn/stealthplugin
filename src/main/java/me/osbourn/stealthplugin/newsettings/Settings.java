package me.osbourn.stealthplugin.newsettings;

public final class Settings {
    private Settings() {
    }

    @Setting(name = "timePerRound")
    public static int timePerRound = 300;
}
