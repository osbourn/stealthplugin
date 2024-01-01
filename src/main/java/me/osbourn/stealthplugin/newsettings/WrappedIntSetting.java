package me.osbourn.stealthplugin.newsettings;

import org.apache.commons.lang.NotImplementedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedIntSetting implements WrappedSetting {
    private final Field field;

    public WrappedIntSetting(Field field) {
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
        try {
            return Integer.toString(this.field.getInt(null));
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object toConfigValue() {
        throw new NotImplementedException();
    }

    @Override
    public void setFromConfigValue() {
        throw new NotImplementedException();
    }
}
