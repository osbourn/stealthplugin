package me.osbourn.stealthplugin.settingsapi;

import java.util.List;
import java.util.Optional;

public interface Setting {
    /**
     * The name of the setting, not containing any spaces
     */
    String getName();

    /**
     * The string to be displayed when the user runs the settings command without providing a value
     *
     * @return
     */
    String getInfo();

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

}
