package me.osbourn.stealthplugin.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.osbourn.stealthplugin.StealthPlugin;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import me.osbourn.stealthplugin.settingsapi.LocationSetting;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PasteStructureCommand implements CommandExecutor {
    private final StealthPlugin plugin;
    private final LocationSetting positionSettings;
    private final BooleanSetting killEntitiesBeforePasteSetting;

    public PasteStructureCommand(StealthPlugin plugin, LocationSetting positionSettings, BooleanSetting killEntitiesBeforePasteSetting) {
        this.plugin = plugin;
        this.positionSettings = positionSettings;
        this.killEntitiesBeforePasteSetting = killEntitiesBeforePasteSetting;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        return pasteStructure(sender);
    }

    public boolean pasteStructure(@NotNull CommandSender sender) {

        // Load schematic
        Clipboard clipboard;
        try {
            File file = new File(plugin.getDataFolder() + "/house.schem");
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            ClipboardReader reader = format.getReader(new FileInputStream(file));
            clipboard = reader.read();
        } catch (FileNotFoundException e) {
            sender.sendMessage("File not found");
            return false;
        } catch (IOException e) {
            sender.sendMessage("IO Exception occurred");
            e.printStackTrace();
            return false;
        }

        // Kill entities before paste
        if (this.killEntitiesBeforePasteSetting.isActive()) {
            World overworld = Bukkit.getWorlds().get(0);
            for (Entity entity : overworld.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }

        World overworld = Bukkit.getWorlds().get(0);
        com.sk89q.worldedit.world.World adaptedOverworld = BukkitAdapter.adapt(overworld);
        int x = this.positionSettings.x();
        int y = this.positionSettings.y();
        int z = this.positionSettings.z();
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedOverworld)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            sender.sendMessage("WorldEdit threw an exception");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
