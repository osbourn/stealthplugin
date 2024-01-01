package me.osbourn.stealthplugin.newsettings;

import me.osbourn.stealthplugin.util.NullableBlockPosition;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedNullableBlockPositionSetting implements WrappedSetting {
    private final Field field;

    public WrappedNullableBlockPositionSetting(Field field) {
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
        NullableBlockPosition pos;
        if (s.equalsIgnoreCase("unset")) {
            pos = NullableBlockPosition.UNSET;
        } else {
            String[] coords = s.split(" +");
            if (coords.length != 3) {
                return SettingChangeResult.fail(String.format("\"%s\" is not a valid block position", s));
            }
            try {
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2]);
                pos = NullableBlockPosition.fromCoords(x, y, z);
            } catch(NumberFormatException e) {
                return SettingChangeResult.fail(String.format("\"%s\" is not a valid block position", s));
            }
        }

        try {
            field.set(null, pos);
            return SettingChangeResult.success(String.format(
                    "Changed \"%s\" to \"%s\"", this.getName(), pos.toStringWithSpaces()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String valueAsString() {
        try {
            return ((NullableBlockPosition) this.field.get(null)).toStringWithSpaces();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object toConfigValue() {
        return null;
    }

    @Override
    public void setFromConfigValue() {

    }
}
