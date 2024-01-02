package me.osbourn.stealthplugin.settings;

import me.osbourn.stealthplugin.StealthPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedBooleanSetting implements WrappedSetting {
    private final Field field;

    public WrappedBooleanSetting(Field field) {
        this.field = field;
        assert field.isAnnotationPresent(Setting.class);
        assert Modifier.isStatic(field.getModifiers());
    }

    private boolean value() {
        try {
            return this.field.getBoolean(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void set(boolean newValue) {
        try {
            this.field.setBoolean(null, newValue);
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
            if (s.equals("true")) {
                this.field.setBoolean(null, true);
                return new SettingChangeResult(true, "Changed \"" + this.getName() + "\" to true");
            } else if (s.equals("false")) {
                this.field.setBoolean(null, false);
                return new SettingChangeResult(true, "Changed \"" + this.getName() + "\" to false");
            } else {
                return new SettingChangeResult(false, "\"" + s + "\" must be true or false");
            }
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String valueAsString() {
        return Boolean.toString(this.value());
    }

    @Override
    public Object toConfigValue() {
        return this.value();
    }

    @Override
    public void setFromConfigValue(@Nullable Object value) {
        if (value instanceof Boolean b) {
            this.set(b);
        } else {
            StealthPlugin.LOGGER.warning("Setting \"" + this.getName() + "\" failed to load from config");
        }
    }

    @Override
    public String[] tabCompletionOptions(String[] currentArgs) {
        return currentArgs.length == 0 ? new String[]{"true", "false"} : new String[0];
    }
}
