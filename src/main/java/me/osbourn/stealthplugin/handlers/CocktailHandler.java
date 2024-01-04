package me.osbourn.stealthplugin.handlers;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
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
    private static final Color UNLIT_COCKTAIL_COLOR = Color.fromRGB(131, 84, 50);
    private static final Color LIT_COCKTAIL_COLOR = Color.fromRGB(131, 84, 50);

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

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> onFuseRunOut(cocktailId), 60);
                return true;
            }
        }
        return false;
    }

    private void onFuseRunOut(long cocktailId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                ItemStack itemStack = player.getInventory().getItem(slot);
                if (matchesCocktailId(itemStack, cocktailId)) {
                    player.setFireTicks(200);
                    player.getInventory().setItem(slot, null);
                    createFireCylinder(player.getLocation(), 3, 1);
                }
            }
        }

        for (World world : Bukkit.getWorlds()) {
            for (Item itemEntity : world.getEntitiesByClass(Item.class)) {
                if (matchesCocktailId(itemEntity.getItemStack(), cocktailId)) {
                    createFireCylinder(itemEntity.getLocation(), 3, 1);
                    itemEntity.remove();
                }
            }
            for (ThrownPotion thrownPotion : world.getEntitiesByClass(ThrownPotion.class)) {
                if (matchesCocktailId(thrownPotion.getItem(), cocktailId)) {
                    createFireCylinder(thrownPotion.getLocation(), 3, 1);
                    thrownPotion.remove();
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

    private void createFireCylinder(Location center, int radius, int heightRadius) {
        if (radius < 0 || heightRadius < 0) throw new IllegalArgumentException();
        World world = center.getWorld();
        if (world == null) throw new IllegalArgumentException();
        for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
            for (int y = center.getBlockY() - heightRadius; y <= center.getBlockY() + heightRadius; y++) {
                for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {
                    int deltaX = center.getBlockX() - x;
                    int deltaZ = center.getBlockZ() - z;
                    int distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                    if (distanceSquared <= radius * radius) {
                        if (world.getBlockAt(x, y, z).getType().isAir()) {
                            BlockData blockData = Material.FIRE.createBlockData();
                            world.setBlockData(x, y, z, blockData);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ItemStack item = event.getPotion().getItem();
        if (item.getType() == Material.SPLASH_POTION) {
            var itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                var persistentDataContainer = itemMeta.getPersistentDataContainer();
                if (persistentDataContainer.has(this.cocktailIdKey, PersistentDataType.LONG)) {
                    createFireCylinder(event.getPotion().getLocation(), 3, 1);
                    event.setCancelled(true);
                }
            }
        }
    }
}
