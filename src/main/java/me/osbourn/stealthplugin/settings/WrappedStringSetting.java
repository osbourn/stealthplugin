package me.osbourn.stealthplugin.settings;

import me.osbourn.stealthplugin.StealthPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedStringSetting implements WrappedSetting {
    private final Field field;

    private String value() {
        try {
            return (String) this.field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void set(String newValue) {
        try {
            this.field.set(null, newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public WrappedStringSetting(Field field) {
        this.field = field;
        assert field.isAnnotationPresent(Setting.class);
        assert Modifier.isStatic(field.getModifiers());
    }

    @Override
    public String getName() {
        return field.getAnnotation(Setting.class).name();
    }

    @Override
    public SettingChangeResult setFromString(String s) {
        try {
            this.field.set(null, s);
            return new SettingChangeResult(true, "Changed \"" + this.getName() + "\" to \"" + s + "\"");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String valueAsString() {
        return this.value();
    }

    @Override
    public Object toConfigValue() {
        return this.value();
    }

    @Override
    public void setFromConfigValue(@Nullable Object value) {
        if (value instanceof String s) {
            this.set(s);
        } else {
            StealthPlugin.LOGGER.warning("Setting \"" + this.getName() + "\" failed to load from config");
        }
    }
}
