package me.osbourn.stealthplugin.settings;

import me.osbourn.stealthplugin.StealthPlugin;
import me.osbourn.stealthplugin.util.NullableBlockPosition;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedNullableBlockPositionSetting implements WrappedSetting {
    private final Field field;

    public WrappedNullableBlockPositionSetting(Field field) {
        this.field = field;
        assert field.isAnnotationPresent(Setting.class);
        assert Modifier.isStatic(field.getModifiers());
    }

    private NullableBlockPosition value() {
        try {
            return (NullableBlockPosition) this.field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void set(NullableBlockPosition newValue) {
        try {
            this.field.set(null, newValue);
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

        this.set(pos);
        return SettingChangeResult.success(String.format(
                "Changed \"%s\" to \"%s\"", this.getName(), pos.toStringWithSpaces()));

    }

    @Override
    public String valueAsString() {
        return this.value().toStringWithSpaces();
    }

    @Override
    public Object toConfigValue() {
        return this.value().toStringWithSpaces();
    }

    @Override
    public void setFromConfigValue(@Nullable Object value) {
        if (value instanceof String s) {
            var result = this.setFromString(s);
            if (result.wasSuccessful()) {
                return;
            }
        }

        StealthPlugin.LOGGER.warning("Setting \"" + this.getName() + "\" failed to load from config");
    }

    @Override
    public String[] tabCompletionOptions(String[] currentArgs) {
        if (currentArgs.length == 0) {
            return new String[]{"unset", "~", "0"};
        } else if (currentArgs.length <= 2 && !currentArgs[0].equals("unset")) {
            return new String[]{"~", "0"};
        } else {
            return new String[0];
        }
    }

    @Override
    public boolean acceptsTildeExpressions() {
        return true;
    }
}
