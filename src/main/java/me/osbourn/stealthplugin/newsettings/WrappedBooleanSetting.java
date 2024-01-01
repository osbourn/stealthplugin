package me.osbourn.stealthplugin.newsettings;

import org.apache.commons.lang.NotImplementedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedBooleanSetting implements WrappedSetting {
    private final Field field;

    public WrappedBooleanSetting(Field field) {
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
        try {
            return Boolean.toString(this.field.getBoolean(null));
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
