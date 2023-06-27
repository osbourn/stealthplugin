package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.GameManager;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.util.AnnouncementUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PreventPrematureTargetDestructionHandler extends BooleanSetting implements Listener {
    private final GameManager gameManager;

    public PreventPrematureTargetDestructionHandler(GameManager gameManager) {
        super("preventprematuretargetdestruction", true);
        this.gameManager = gameManager;
    }

    private boolean shouldPreventDestruction(Material material) {
        boolean isPossibleTarget = gameManager.getGameTargets().getAvailableTargets().contains(material);
        return isPossibleTarget && this.gameManager.isPrepTime();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isActive() && shouldPreventDestruction(event.getBlock().getType())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break objectives during prep time!");
        }
    }

    /*
     * For explosions caused by TNT and other entities.
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.isActive()) {
            boolean didRemove = event.blockList().removeIf(b -> shouldPreventDestruction(b.getType()));
            if (didRemove) {
                AnnouncementUtils.announceToDefenders(gameManager, ChatColor.RED + "You cannot destroy objectives during prep time!");
            }
        }
    }

    /*
     * For explosions caused by Respawn Anchor and other blocks.
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (this.isActive()) {
            boolean didRemove = event.blockList().removeIf(b -> shouldPreventDestruction(b.getType()));
            if (didRemove) {
                AnnouncementUtils.announceToDefenders(gameManager, ChatColor.RED + "You cannot destroy objectives during prep time!");
            }
        }
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (this.isActive() && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
                && event.getClickedBlock() != null) {
            Material material = event.getClickedBlock().getType();
            if (shouldPreventDestruction(material)) {
                // Only disable interactions with specific blocks because you can't build off the target if interactions are disabled
                if (material == Material.RESPAWN_ANCHOR || material == Material.DRAGON_EGG) {
                    event.setCancelled(true);
                    AnnouncementUtils.announceToDefenders(gameManager, ChatColor.RED + "You cannot interact with objectives during prep time!");
                }
            }
        }
    }
}
