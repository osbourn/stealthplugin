package me.osbourn.stealthplugin.handlers;


import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Objects;

public class CocktailHandler implements Listener {
    private static final Color UNLIT_COCKTAIL_COLOR = Color.fromRGB(200, 200, 200);
    private static final Color LIT_COCKTAIL_COLOR = Color.fromRGB(200, 0, 0);

    @EventHandler
    public void lightCocktail(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        EquipmentSlot flintAndSteelSlot = null;
        EquipmentSlot possiblePotionSlot = null;
        if (player.getInventory().getItemInMainHand().getType() == Material.FLINT_AND_STEEL) {
            flintAndSteelSlot = EquipmentSlot.HAND;
            possiblePotionSlot = EquipmentSlot.OFF_HAND;
        } else if (player.getInventory().getItemInOffHand().getType() == Material.FLINT_AND_STEEL) {
            flintAndSteelSlot = EquipmentSlot.OFF_HAND;
            possiblePotionSlot = EquipmentSlot.HAND;
        }
        if (flintAndSteelSlot != null) {
            boolean didLightCocktail = lightCocktailIfAppropriate(player, possiblePotionSlot);
            if (didLightCocktail) {
                event.setCancelled(true);
            }
        }
    }

    private boolean lightCocktailIfAppropriate(Player player, EquipmentSlot slot) {
        ItemStack heldItem = player.getInventory().getItem(slot);
        if (heldItem != null && heldItem.getType() == Material.POTION) {
            PotionMeta potionMeta = (PotionMeta) heldItem.getItemMeta();
            if (potionMeta != null && potionMeta.hasColor() && Objects.equals(potionMeta.getColor(), UNLIT_COCKTAIL_COLOR)) {
                ItemStack newPotion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta newPotionMeta = potionMeta.clone();
                newPotionMeta.setColor(LIT_COCKTAIL_COLOR);
                newPotion.setItemMeta(newPotionMeta);
                player.getInventory().setItem(slot, newPotion);
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
    }
}
