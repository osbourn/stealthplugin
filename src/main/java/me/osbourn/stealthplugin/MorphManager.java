package me.osbourn.stealthplugin;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MorphManager {
    private final Map<UUID,UUID> morphs;

    public MorphManager() {
        this.morphs = new HashMap<>();
    }

    public boolean isUUIDMorphed(UUID uuid) {
        return this.morphs.containsKey(uuid);
    }

    public boolean isPlayerMorphed(Player player) {
        return this.morphs.containsKey(player.getUniqueId());
    }

    public LivingEntity getMorphedEntity(Player player) {
        if (!isPlayerMorphed(player)) {
            throw new IllegalStateException("Player " + player.getName() + " is not morphed");
        }

        UUID targetUUID = this.morphs.get(player.getUniqueId());
        Object[] matching = player.getWorld().getEntities().stream()
                .filter(entity -> entity.getUniqueId().equals(targetUUID))
                .toArray();
        return (LivingEntity) matching[0];
    }

    public void morphAsEntity(Player player, LivingEntity target) {
        if (this.isPlayerMorphed(player)) {
            throw new IllegalStateException("Player " + player.getName() + " is already morphed");
        }

        morphs.put(player.getUniqueId(), target.getUniqueId());
        target.setAI(false);
        player.setInvisible(true);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
                target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }
}
