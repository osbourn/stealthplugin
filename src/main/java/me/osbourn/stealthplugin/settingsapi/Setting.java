package me.osbourn.stealthplugin.settingsapi;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface Setting {
    /**
     * The name of the setting, not containing any spaces
     */
    String getName();

    /**
     * Convert the data stored in the setting to a user-friendly string
     */
    String valueAsString();

    /**
     * The string to be displayed when the user runs the settings command without providing a value
     */
    default String getInfoMessage() {
        return String.format("%s is currently set to %s", this.getName(), this.valueAsString());
    };

    /**
     * The string to be displayed when the user sets the value of the setting using the /settings command
     */
    default String getSetMessage() {
        return String.format("%s is now set to %s", this.getName(), this.valueAsString());
    }

    /**
     * Set the setting to the specified value. Pass in an array containing the final arguments of a command.
     * arguments will contain 1 value in most cases, usually true or false, but can accept 3 for positions, for example.
     *
     * @return The error message when an invalid setting is provided, or None if it is a success.
     */
    Optional<String> trySet(String[] arguments);

    /**
     * The list of options that will be displayed by tab complete, an empty list by default
     */
    default List<String> tabCompletionOptions() {
        return List.of();
    }

    Object configValue();

    void setFromConfigValue(@Nullable Object value);
}
