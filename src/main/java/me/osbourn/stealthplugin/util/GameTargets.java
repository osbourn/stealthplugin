package me.osbourn.stealthplugin.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

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
        this.availableTargets.add(Material.LODESTONE);
        this.availableTargets.add(Material.JUKEBOX);
        this.availableTargets.add(Material.DRAGON_EGG);
        this.availableTargets.add(Material.BUDDING_AMETHYST);
        this.availableTargets.add(Material.GILDED_BLACKSTONE);
        this.availableTargets.add(Material.JACK_O_LANTERN);
        this.availableTargets.add(Material.CAKE);
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

    /**
     * Like #registerAsBroken, but doesn't throw an exception if the material isn't active
     */
    public void registerAsBrokenIfActive(Material material) {
        if (this.activeTargets.contains(material)) {
            this.registerAsBroken(material);
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

    /**
     * The chat message that should be sent to defenders at the start of the game
     */
    public BaseComponent[] getSelectionMessage() {
        var builder =  new ComponentBuilder("Which targets would you like?\n");
        for (Material material : this.getAvailableTargets()) {
            String prettyMaterialName = MaterialsUtil.prettyMaterialName(material.toString());
            TextComponent textComponent = new TextComponent("[" + prettyMaterialName + "]");
            String command = "selecttarget " + material;
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
            textComponent.setColor(ChatColor.GREEN);
            builder.append(textComponent).append(" ");
        }
        return builder.create();
    }

    /**
     * If defenders have not selected all their objectives, choose random objectives from the remaining ones available
     * to use for this round.
     *
     * @param numTargets The number of targets to use in this game. Should probably be the same value as the
     *                   numberoftargets setting.
     * @return A list of the targets that were randomly selected (empty if defenders manually selected all their targets)
     */
    public List<String> fillRemainingTargetSlots(int numTargets) {
        if (this.activeTargets.size() < numTargets) {
            Set<Material> currentlyActiveTargets = new HashSet<>(this.activeTargets);
            // The list returned from the stream is unmodifiable, so we make a copy of it
            List<Material> unchosenTargets = new ArrayList<>(this.availableTargets.stream()
                    .filter(target -> !currentlyActiveTargets.contains(target))
                    .toList());
            // This could probably be made more efficient, but it doesn't matter too much if there aren't that many
            // available objectives.
            Collections.shuffle(unchosenTargets);
            // min is needed in some cases to prevent subList from throwing an IndexOutOfBoundsException
            int numToAdd = Integer.min(numTargets - currentlyActiveTargets.size(), unchosenTargets.size());
            List<Material> toAdd = unchosenTargets.subList(0, numToAdd);
            this.activeTargets.addAll(toAdd);
            return toAdd.stream().map(target -> MaterialsUtil.prettyMaterialName(target.toString())).toList();
        } else {
            return List.of();
        }
    }
}
