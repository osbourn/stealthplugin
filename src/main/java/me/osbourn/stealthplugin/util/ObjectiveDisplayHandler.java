
package me.osbourn.stealthplugin.util;

import org.bukkit.scoreboard.Objective;

import java.util.List;

public class ObjectiveDisplayHandler {
    public final Objective objective;

    private List<String> mostRecentlyAppliedEntries = null;

    public ObjectiveDisplayHandler(Objective objective) {
        this.objective = objective;
    }

    public void updateObjective(List<String> entries) {
        /*
            In Minecraft, scoreboards display the names of players followed by their score.
            They only show the players who have a score assigned to them and they organize the players by their score.
            For example, a scoreboard might look like this:
                ObjectiveName
                Steve          10
                Alex            8
                Bob             4
                David           0
            (Note how the players with the highest score appear on top.)
            Spigot lets us display whatever text we want on the scoreboard because the players on the scoreboard don't
            have to be real players. We can use fake player names like "Time: 1:40:07" which we assign a score to so
            that they display on the scoreboard.
            For example, a scoreboard that displays fake names might look like:
                ObjectiveName
                Time: 1:50:07   6
                Overworld:      5
                SomeLineOfText  4
                MoreText        3
                Nether:         2
                EvenMoreText    1
            The clients think that "Time: 1:50:07", "Overworld:", "SomeLineOfText", etc. are all player names.
            Note that if you want to remove a line from the scoreboard, you just have to clear its score.

            This method takes in a list of lines that should be displayed on the scoreboard and displays them.
            First it clears the scoreboard by clearing the score of all the lines that were entered last time.
            Next it adds each line to the scoreboard by setting its score to be one less than the previous line.
         */

        // Clear lines from scoreboard
        if (mostRecentlyAppliedEntries != null) {
            for (String entry : mostRecentlyAppliedEntries) {
                objective.getScoreboard().resetScores(entry);
            }
        }

        // Add line to scoreboard
        for (int i = 0; i < entries.size(); i++) {
            int score = entries.size() - i;
            objective.getScore(entries.get(i)).setScore(score);
        }

        mostRecentlyAppliedEntries = entries;
    }
}
