package me.osbourn.stealthplugin.newsettings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

public class SettingsManager {
    private Class<?> clazz;

    public SettingsManager(Class<?> clazz) {
        this.clazz = clazz;
    }

    private static WrappedSetting getWrappedSettingFromField(Field field) {
        if (!field.isAnnotationPresent(Setting.class)) {
            throw new AssertionError();
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new AssertionError();
        }

        if (field.getType().equals(Integer.TYPE)) {
            return new WrappedIntSetting(field);
        }
        throw new IllegalArgumentException("Setting field is not of a valid type");
    }

    public Optional<WrappedSetting> getWrappedSetting(String name) {
        Optional<Field> foundField = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.isAnnotationPresent(Setting.class))
                .filter(f -> f.getAnnotation(Setting.class).name().equals(name))
                .findFirst();
        return foundField.map(SettingsManager::getWrappedSettingFromField);
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
