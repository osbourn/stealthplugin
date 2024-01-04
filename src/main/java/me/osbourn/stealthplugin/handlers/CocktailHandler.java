package me.osbourn.stealthplugin.handlers;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class CocktailHandler implements Listener {
    private static final Color UNLIT_COCKTAIL_COLOR = Color.fromRGB(200, 200, 200);
    private static final Color LIT_COCKTAIL_COLOR = Color.fromRGB(200, 0, 0);

    private final JavaPlugin plugin;
    private final NamespacedKey cocktailIdKey;

    public CocktailHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.cocktailIdKey = new NamespacedKey(plugin, "cocktail-id");
    }

    @EventHandler
    public void lightCocktail(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.getTargetBlock(null, 5).getType() == Material.FIRE) {
                boolean didLightCocktailInMainHand = lightCocktailIfAppropriate(player, EquipmentSlot.HAND);
                if (!didLightCocktailInMainHand) {
                    lightCocktailIfAppropriate(player, EquipmentSlot.OFF_HAND);
                }
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
                var persistentDataContainer = newPotionMeta.getPersistentDataContainer();

                newPotionMeta.setColor(LIT_COCKTAIL_COLOR);

                long cocktailId = ThreadLocalRandom.current().nextLong();
                persistentDataContainer.set(this.cocktailIdKey, PersistentDataType.LONG, cocktailId);

                newPotion.setItemMeta(newPotionMeta);
                player.getInventory().setItem(slot, newPotion);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> onFuseEnd(cocktailId), 60);
                return true;
            }
        }
        return false;
    }

    private void onFuseEnd(long cocktailId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                ItemStack itemStack = player.getInventory().getItem(slot);
                if (matchesCocktailId(itemStack, cocktailId)) {
                    player.setFireTicks(200);
                    player.getInventory().setItem(slot, null);
                }
            }
        }

        for (World world : Bukkit.getWorlds()) {
            for (Item itemEntity : world.getEntitiesByClass(Item.class)) {
                if (matchesCocktailId(itemEntity.getItemStack(), cocktailId)) {
                    itemEntity.remove();
                }
            }
        }
    }

    private boolean matchesCocktailId(ItemStack itemStack, long cocktailId) {
        if (itemStack != null && itemStack.getType() == Material.SPLASH_POTION) {
            var itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var persistentDataContainer = itemMeta.getPersistentDataContainer();
                return Objects.equals(persistentDataContainer.get(this.cocktailIdKey, PersistentDataType.LONG), cocktailId);
            }
        }
        return false;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
    }
}
