package me.osbourn.stealthplugin.newsettings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

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
        } else if (field.getType().equals(Boolean.TYPE)) {
            return new WrappedBooleanSetting(field);
        } else if (field.getType().equals(String.class)) {
            return new WrappedStringSetting(field);
        } else {
            throw new IllegalArgumentException("Setting field is not of a valid type");
        }
    }

    public Optional<WrappedSetting> getWrappedSetting(String name) {
        Optional<Field> foundField = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.isAnnotationPresent(Setting.class))
                .filter(f -> f.getAnnotation(Setting.class).name().equals(name))
                .findFirst();
        return foundField.map(SettingsManager::getWrappedSettingFromField);
    }

    public SettingChangeResult changeSetting(String name, String newValue) {
        Optional<WrappedSetting> wrappedSetting = this.getWrappedSetting(name);
        if (wrappedSetting.isPresent()) {
            return wrappedSetting.get().setFromString(newValue);
        } else {
            return new SettingChangeResult(false, "No such setting \"" + name + "\"");
        }
    }

    public String getInfoMessage(String settingName) {
        Optional<WrappedSetting> wrappedSetting = this.getWrappedSetting(settingName);
        if (wrappedSetting.isPresent()) {
            return wrappedSetting.get().infoMessage();
        } else {
            return "No such setting \"" + settingName + "\"";
        }
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
