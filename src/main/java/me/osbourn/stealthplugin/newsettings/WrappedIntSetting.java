package me.osbourn.stealthplugin.newsettings;

import me.osbourn.stealthplugin.StealthPlugin;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedIntSetting implements WrappedSetting {
    private final Field field;

    public WrappedIntSetting(Field field) {
        this.field = field;
        assert field.isAnnotationPresent(Setting.class);
        assert Modifier.isStatic(field.getModifiers());
    }

    private int value() {
        try {
            return this.field.getInt(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void set(int newValue) {
        try {
            this.field.setInt(null, newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return field.getAnnotation(Setting.class).name();
    }

    @Override
    public SettingChangeResult setFromString(String s) {
        try {
            int x = Integer.parseInt(s);
            this.field.setInt(null, x);
            return new SettingChangeResult(true, "Changed \"" + this.getName() + "\" to \"" + x + "\"");
        } catch(NumberFormatException e) {
            return new SettingChangeResult(false, "\"" + s + "\" is not a valid integer");
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String valueAsString() {
        return Integer.toString(this.value());
    }

    @Override
    public Object toConfigValue() {
        return this.value();
    }

    @Override
    public void setFromConfigValue(@Nullable Object value) {
        if (value instanceof Integer x) {
            this.set(x);
        } else {
            StealthPlugin.LOGGER.warning("Setting \"" + this.getName() + "\" failed to load from config");
        }
    }
}
