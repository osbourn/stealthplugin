package me.osbourn.stealthplugin.util;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameTargets {
    /**
     * List of targets that the defending team can choose (persists through game rounds)
     */
    private final List<Material> availableTargets;

    /**
     * List of the targets that the defending team has chosen for this game (resets every game round)
     */
    private final List<Material> activeTargets;

    /**
     * Set of found blocks (resets every game round)
     */
    private final Set<Material> brokenTargets;

    public GameTargets() {
        this.availableTargets = new ArrayList<>();
        this.activeTargets = new ArrayList<>();
        this.brokenTargets = new HashSet<>();
        this.availableTargets.add(Material.RESPAWN_ANCHOR);
        this.availableTargets.add(Material.ENCHANTING_TABLE);
        this.availableTargets.add(Material.ENDER_CHEST);
        this.availableTargets.add(Material.CONDUIT);
    }

    /**
     * List of targets that the defenders can choose from
     */
    public List<Material> getAvailableTargets() {
        return this.availableTargets;
    }

    /**
     * List of targets to be used in this game session
     */
    public List<Material> getActiveTargets() {
        return this.activeTargets;
    }

    public boolean hasBeenBroken(Material material) {
        return this.brokenTargets.contains(material);
    }

    public void registerAsBroken(Material material) {
        if (this.activeTargets.contains(material)) {
            this.brokenTargets.add(material);
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public void resetSelectedTargets() {
        this.activeTargets.clear();
        this.resetBrokenTargets();
    }

    public void resetBrokenTargets() {
        this.brokenTargets.clear();
    }

    public int numRemaining() {
        return activeTargets.size() - brokenTargets.size();
    }

    public boolean allTargetsBroken() {
        // If there are no targets in the first place, don't count it as a win for the attackers
        return numRemaining() == 0 && activeTargets.size() >= 1;
    }
}
