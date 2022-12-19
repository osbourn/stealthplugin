package me.osbourn.stealthplugin;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MorphManager implements Listener {
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
        target.setInvulnerable(true);
        player.setInvisible(true);
        player.getCollidableExemptions().add(target.getUniqueId());
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
                target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setHealth(target.getHealth());
    }

    public void unmorph(Player player) {
        if (!isPlayerMorphed(player)) {
            throw new IllegalStateException("Player " + player.getName() + " is not morphed");
        }

        LivingEntity target = this.getMorphedEntity(player);
        target.setHealth(0.0);
        player.setInvisible(false);
        player.getCollidableExemptions().remove(target.getUniqueId());
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);

        // Don't resurrect player if they unmorphed because of death
        if (player.getHealth() > 0.0) {
            player.setHealth(20.0);
        }

        this.morphs.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isPlayerMorphed(player)) {
            Location loc = event.getTo();
            LivingEntity entity = this.getMorphedEntity(player);
            entity.teleport(loc);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (isPlayerMorphed(player)) {
            event.setDeathMessage("Morphed player " + event.getDeathMessage());
            this.unmorph(player);
        }
    }
}
