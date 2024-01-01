package me.osbourn.stealthplugin.newsettings;

import org.apache.commons.lang.NotImplementedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedStringSetting implements WrappedSetting {
    private final Field field;

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
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
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
