package me.osbourn.stealthplugin.newsettings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SettingsManager {
    private Class<?> clazz;

    public SettingsManager(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void printDebugInfo() {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(Setting.class)) {
                Setting setting = field.getAnnotation(Setting.class);
                System.out.println(setting.name());
                System.out.println(field.toString());
            }
        }
    }
}
