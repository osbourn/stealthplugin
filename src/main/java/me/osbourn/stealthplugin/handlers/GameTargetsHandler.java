package me.osbourn.stealthplugin.handlers;

import me.osbourn.stealthplugin.MorphManager;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.util.GameTargets;
import me.osbourn.stealthplugin.util.MaterialsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class GameTargetsHandler extends BooleanSetting implements Listener {
    private final GameTargets gameTargets;
    private final MorphManager morphManager;

    public GameTargetsHandler(GameTargets gameTargets, MorphManager morphManager) {
        super("announcebrokentargets", true);
        this.gameTargets = gameTargets;
        this.morphManager = morphManager;
    }

    private void announceDestruction(Material material, String adjective) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(String.format("%s %s", MaterialsUtil.prettyMaterialName(material.toString()), adjective));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material brokenBlockType = event.getBlock().getType();
        if (gameTargets.getTargetMaterials().contains(brokenBlockType)) {
            gameTargets.registerAsBroken(brokenBlockType);
            if (this.isActive()) {
                announceDestruction(brokenBlockType, "was broken");
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            Material brokenBlockType = block.getType();
            if (gameTargets.getTargetMaterials().contains(brokenBlockType)) {
                gameTargets.registerAsBroken(brokenBlockType);
                if (this.isActive()) {
                    announceDestruction(brokenBlockType, "was blown up");
                }
            }
        }
    }
}
