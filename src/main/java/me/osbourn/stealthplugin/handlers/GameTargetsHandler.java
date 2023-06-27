package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.MorphManager;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.util.AnnouncementUtils;
import me.osbourn.stealthplugin.util.GameTargets;
import me.osbourn.stealthplugin.util.MaterialsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameTargetsHandler extends BooleanSetting implements Listener {
    private final GameTargets gameTargets;
    private final MorphManager morphManager;

    public GameTargetsHandler(GameTargets gameTargets, MorphManager morphManager) {
        super("announcebrokentargets", true);
        this.gameTargets = gameTargets;
        this.morphManager = morphManager;
    }

    private void announceDestruction(Material material, String adjective) {
        AnnouncementUtils.announce(String.format("%s %s", MaterialsUtil.prettyMaterialName(material.toString()), adjective));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Material brokenBlockType = event.getBlock().getType();
            if (gameTargets.getAvailableTargets().contains(brokenBlockType)) {
                gameTargets.registerAsBroken(brokenBlockType);
                if (this.isActive()) {
                    announceDestruction(brokenBlockType, "was broken");
                }
            }
        }
    }

    /*
     * For explosions caused by TNT and other entities.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!event.isCancelled()) {
            for (Block block : event.blockList()) {
                Material brokenBlockType = block.getType();
                if (gameTargets.getAvailableTargets().contains(brokenBlockType)) {
                    gameTargets.registerAsBroken(brokenBlockType);
                    if (this.isActive()) {
                        announceDestruction(brokenBlockType, "was blown up");
                    }
                }
            }
        }
    }

    /*
     * For explosions caused by Respawn Anchor and other blocks.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!event.isCancelled()) {
            for (Block block : event.blockList()) {
                Material brokenBlockType = block.getType();
                if (gameTargets.getAvailableTargets().contains(brokenBlockType)) {
                    gameTargets.registerAsBroken(brokenBlockType);
                    if (this.isActive()) {
                        announceDestruction(brokenBlockType, "was blown up");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if (event.useInteractedBlock() != Event.Result.DENY && block != null) {
            if (action == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.RESPAWN_ANCHOR) {
                if (gameTargets.getAvailableTargets().contains(Material.RESPAWN_ANCHOR)) {
                    if (block.getBlockData() instanceof RespawnAnchor anchor) {
                        // TODO: Fix clicking the respawn anchor several times with glowstone in the offhand counting as a break
                        if (anchor.getCharges() > 0 && (event.getItem() == null || event.getItem().getType() != Material.GLOWSTONE)) {
                            if(gameTargets.getActiveTargets().contains(Material.RESPAWN_ANCHOR)) {
                                gameTargets.registerAsBroken(Material.RESPAWN_ANCHOR);
                                if (this.isActive()) {
                                    announceDestruction(Material.RESPAWN_ANCHOR, "was filled with glowstone and clicked");
                                }
                            }
                        }
                    }
                }
            }

            if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) &&
                    block.getType() == Material.DRAGON_EGG) {
                if (this.gameTargets.getActiveTargets().contains(Material.DRAGON_EGG)) {
                    gameTargets.registerAsBroken(Material.DRAGON_EGG);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.getClickedBlock().setType(Material.AIR);
                    if (this.isActive()) {
                        announceDestruction(Material.DRAGON_EGG, "was teleported out of existence");
                    }
                }
            }
        }
    }
}
