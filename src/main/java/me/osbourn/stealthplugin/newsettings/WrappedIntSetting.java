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
    public void setFromString() {
        throw new NotImplementedException();
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
