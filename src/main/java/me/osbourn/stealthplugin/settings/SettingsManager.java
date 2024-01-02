package me.osbourn.stealthplugin.settings;

import me.osbourn.stealthplugin.util.NullableBlockPosition;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SettingsManager {
    private final Class<?> clazz;
    private final JavaPlugin plugin;

    public SettingsManager(Class<?> clazz, JavaPlugin plugin) {
        this.clazz = clazz;
        this.plugin = plugin;
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
        } else if (field.getType().equals(NullableBlockPosition.class)) {
            return new WrappedNullableBlockPositionSetting(field);
        } else {
            throw new IllegalArgumentException("Setting field is not of a valid type");
        }
    }

    /**
     * Saves a setting associated with a given field.
     * Remember to save the config after all writing to the settings has been done.
     */
    private void saveSetting(Field field) {
        WrappedSetting wrapper = getWrappedSettingFromField(field);
        String settingName = field.getAnnotation(Setting.class).name();
        Object valueToStore = wrapper.toConfigValue();
        this.plugin.getConfig().set(settingName, valueToStore);
    }

    public void saveSettings() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Setting.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    this.saveSetting(field);
                }
            }
        }
        this.plugin.saveConfig();
    }

    private void loadSetting(Field field) {
        WrappedSetting wrapper = getWrappedSettingFromField(field);
        String settingName = field.getAnnotation(Setting.class).name();
        Object valueInConfig = this.plugin.getConfig().get(settingName);
        wrapper.setFromConfigValue(valueInConfig);
    }

    public void loadSettings() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Setting.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    this.loadSetting(field);
                }
            }
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

    public List<String> getSettingNames() {
         return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.isAnnotationPresent(Setting.class))
                .map(f -> f.getAnnotation(Setting.class).name())
                .toList();
    }

    public String[] getTabCompletionOptions(String settingName, String[] currentArgs) {
        Optional<WrappedSetting> wrappedSetting = this.getWrappedSetting(settingName);
        if (wrappedSetting.isPresent()) {
            return wrappedSetting.get().tabCompletionOptions(currentArgs);
        } else {
            return new String[0];
        }
    }

    public boolean acceptsTildeExpressions(String settingName) {
        Optional<WrappedSetting> wrappedSetting = this.getWrappedSetting(settingName);
        return wrappedSetting.isPresent() && wrappedSetting.get().acceptsTildeExpressions();
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
