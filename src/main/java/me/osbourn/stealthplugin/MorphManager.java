package me.osbourn.stealthplugin;

import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MorphManager implements Listener {
    private final Map<UUID, UUID> morphs;
    private final BooleanSetting morphedPlayersCanAttackSetting;
    private final BooleanSetting morphedPlayersIgnoreArrowsSetting;

    public MorphManager(BooleanSetting morphedPlayersCanAttackSetting, BooleanSetting morphedPlayersIgnoreArrowsSetting) {
        this.morphs = new HashMap<>();
        this.morphedPlayersCanAttackSetting = morphedPlayersCanAttackSetting;
        this.morphedPlayersIgnoreArrowsSetting = morphedPlayersIgnoreArrowsSetting;
    }

    public boolean isUUIDMorphed(UUID uuid) {
        return this.morphs.containsKey(uuid);
    }

    public boolean isPlayerMorphed(Player player) {
        return this.morphs.containsKey(player.getUniqueId());
    }

    public @Nullable LivingEntity getMorphedEntity(Player player) {
        if (!isPlayerMorphed(player)) {
            throw new IllegalStateException("Player " + player.getName() + " is not morphed");
        }

        UUID targetUUID = this.morphs.get(player.getUniqueId());
        Object[] matching = player.getWorld().getEntities().stream()
                .filter(entity -> entity.getUniqueId().equals(targetUUID))
                .toArray();
        if (matching.length != 1) {
            this.fastUnmorph(player);
            player.sendMessage("Morphed entity not found");
            return null;
        } else {
            return (LivingEntity) matching[0];
        }
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

    /**
     * If the morphed entity is not found, the player should be quickly unmorphed to avoid constant errors to console
     */
    public void fastUnmorph(Player player) {
        player.setInvisible(false);
        player.getCollidableExemptions().remove(this.morphs.get(player.getUniqueId()));
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        this.morphs.remove(player.getUniqueId());
    }

    public void unmorph(Player player) {
        if (!isPlayerMorphed(player)) {
            throw new IllegalStateException("Player " + player.getName() + " is not morphed");
        }

        LivingEntity target = this.getMorphedEntity(player);
        player.setInvisible(false);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        if (target != null) {
            target.setHealth(0.0);
            player.getCollidableExemptions().remove(target.getUniqueId());
        }

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
            if (entity != null) {
                entity.teleport(loc);
            }
        }
    }

    // Needs to have higher priority than PlayersDropArrowsHandler
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (isPlayerMorphed(player)) {
            event.setDeathMessage(ChatColor.DARK_GRAY + "Morphed player " + event.getDeathMessage());
            this.unmorph(player);
        }
    }

    @EventHandler
    public void pickupItemEvent(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (this.isPlayerMorphed(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void pickupArrowEvent(PlayerPickupArrowEvent event) {
        if (this.isPlayerMorphed(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void breakBlockEvent(BlockBreakEvent event) {
        if (this.isPlayerMorphed(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (this.isPlayerMorphed(player)) {
                if (!this.morphedPlayersCanAttackSetting.isActive()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (this.isPlayerMorphed(event.getPlayer())) {
                final Material[] restrictedBlocks = {Material.CHEST, Material.BARREL, Material.HOPPER,
                        Material.TRAPPED_CHEST, Material.SHULKER_BOX, Material.DISPENSER, Material.DROPPER,
                        Material.FURNACE, Material.CAMPFIRE, Material.SOUL_CAMPFIRE};
                Block block = event.getClickedBlock();
                if (Arrays.asList(restrictedBlocks).contains(block.getType())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void arrowHitEvent(ProjectileHitEvent event) {
        if (this.morphedPlayersIgnoreArrowsSetting.isActive()) {
            if (event.getEntity().getType() == EntityType.ARROW) {
                if (event.getHitEntity() != null && event.getHitEntity() instanceof Player player) {
                    if (this.isPlayerMorphed(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
