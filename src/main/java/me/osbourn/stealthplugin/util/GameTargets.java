package me.osbourn.stealthplugin.util;

import org.bukkit.Material;

import java.util.*;

public class GameTargets {
    /**
     * Set of target blocks (persists through game sessions)
     */
    private final List<Material> targetMaterials;
    /**
     * Set of found blocks (resets every game session)
     */
    private final Set<Material> brokenTargets;

    public GameTargets() {
        this.targetMaterials = new ArrayList<>();
        this.brokenTargets = new HashSet<>();
        this.targetMaterials.add(Material.RESPAWN_ANCHOR);
        this.targetMaterials.add(Material.ENCHANTING_TABLE);
    }

    /**
     * A Map associating target blocks to whether or not they have been broken (true if they have been broken)
     */
    public List<Material> getTargetMaterials() {
        return this.targetMaterials;
    }

    public boolean hasBeenBroken(Material material) {
        return this.brokenTargets.contains(material);
    }

    public void registerAsBroken(Material material) {
        if (this.targetMaterials.contains(material)) {
            this.brokenTargets.add(material);
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public void resetBrokenTargets() {
        this.brokenTargets.clear();
    }
}
