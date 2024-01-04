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
import me.osbourn.stealthplugin.settings.Settings;
import me.osbourn.stealthplugin.util.NullableBlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PasteStructureCommand implements CommandExecutor {
    private final StealthPlugin plugin;

    // TODO: Change so that this no longer uses static state
    public static List<Runnable> beforePasteHooks = new ArrayList<>();

    public PasteStructureCommand(StealthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("stealth.manage")) {
            return false;
        }

        if (args.length == 0) {
            return pasteMapStructure(this.plugin, sender);
        }

        return switch (args[0]) {
            case "map" -> pasteMapStructure(this.plugin, sender);
            case "lobby" -> pasteStructure(this.plugin, sender, Settings.lobbyPasteFile, Settings.lobbyPasteLocation);
            default -> false;
        };
    }

    public static boolean pasteMapStructure(JavaPlugin plugin, @Nullable CommandSender sender) {
        return pasteStructure(plugin, sender, Settings.mapPasteFile, Settings.mapPasteLocation);
    }

    /**
     * Pastes the structure at location specified in settings
     * @param plugin Plugin used to find the data folder to look in
     * @param sender Source to send error messages to if pasting the structure fails
     * @param structureFile Name of the structure file, or something starting with "internal/"
     * @return true if the structure pasted correctly, false if there were errors
     */
    public static boolean pasteStructure(JavaPlugin plugin, @Nullable CommandSender sender, String structureFile, NullableBlockPosition pasteLocation) {
        if (!pasteLocation.isSet()) {
            if (sender != null) {
                sender.sendMessage("Structure paste location is not set!");
            }
            return false;
        }

        // Load schematic
        Clipboard clipboard;
        try {
            InputStream is;
            ClipboardFormat format;
            if (structureFile.startsWith("internal/")) {
                String internalName = structureFile.substring("internal/".length());
                is = StealthPlugin.class.getResourceAsStream("/" + internalName);
                format = ClipboardFormats.findByAlias("schem"); // All internal schematics use this format
                assert format != null;
                if (is == null) {
                    if (sender != null) {
                        sender.sendMessage("No such internal file \"" + internalName + "\"");
                    }
                    return false;
                }
            } else {
                File file = new File(plugin.getDataFolder() + "/" + structureFile);
                is = new FileInputStream(file);
                format = ClipboardFormats.findByFile(file);
                if (format == null) {
                    if (sender != null) {
                        sender.sendMessage("Could not detect the file's format");
                    }
                    return false;
                }
            }
            ClipboardReader reader = format.getReader(is);
            clipboard = reader.read();
        } catch (FileNotFoundException e) {
            if (sender != null) {
                sender.sendMessage("No such file \"" + structureFile + "\" in the plugin config directory");
            }
            return false;
        } catch (IOException e) {
            if (sender != null) {
                sender.sendMessage("IO Exception occurred");
            }
            e.printStackTrace();
            return false;
        }

        // Kill entities before paste
        if (Settings.killEntitiesBeforePaste) {
            World overworld = Bukkit.getWorlds().get(0);
            for (Entity entity : overworld.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }
        beforePasteHooks.forEach(Runnable::run);

        World overworld = Bukkit.getWorlds().get(0);
        com.sk89q.worldedit.world.World adaptedOverworld = BukkitAdapter.adapt(overworld);
        int x = pasteLocation.x();
        int y = pasteLocation.y();
        int z = pasteLocation.z();
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedOverworld)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            if (sender != null) {
                sender.sendMessage("WorldEdit threw an exception");
            }
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
