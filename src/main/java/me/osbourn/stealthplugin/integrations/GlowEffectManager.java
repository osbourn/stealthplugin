package me.osbourn.stealthplugin.integrations;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import me.osbourn.stealthplugin.MorphManager;
import me.osbourn.stealthplugin.newsettings.Settings;
import me.osbourn.stealthplugin.settingsapi.BooleanSetting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GlowEffectManager extends PacketAdapter {
    private final MorphManager morphManager;

    public GlowEffectManager(Plugin plugin, MorphManager morphManager) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA);
        this.morphManager = morphManager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        // The code inside this method can be a bit confusing so please read these comments!

        // Teammates should not be glowing if the option is set to false
        if (!Settings.glowingTeammates) {
            return;
        }

        Player receivingPlayer = event.getPlayer();
        PacketContainer packet = event.getPacket();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        /*
         * Get the first integer of the packet.
         * The packet is of type Play.Server.ENTITY_METADATA
         * so the first integer represents the entity id
         */
        int id = packet.getIntegers().read(0);

        Player targetedPlayer = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getEntityId() == id) {
                targetedPlayer = p;
                break;
            }
        }

        // If the targeted entity isn't part of the online players list, don't modify the packet
        if (targetedPlayer == null) {
            return;
        }

        // If either player isn't on a team or they aren't on the same team, don't modify the packet
        Team receivingPlayerTeam = scoreboard.getEntryTeam(receivingPlayer.getName());
        Team targetedPlayerTeam = scoreboard.getEntryTeam(targetedPlayer.getName());
        if (receivingPlayerTeam == null || !receivingPlayerTeam.equals(targetedPlayerTeam)) {
            return;
        }

        // If the packet is targeting the player who is receiving the packet, don't modify the packet
        if (receivingPlayer.equals(targetedPlayer)) {
            return;
        }

        // If the targeted player is morphed, don't modify the packet
        if (this.morphManager.isPlayerMorphed(targetedPlayer)) {
            return;
        }

        /*
         * Data Stuff
         *
         * We are handling packets sent by Minecraft and adjusting them to make sure that appropriate players are glowing
         * Minecraft Packets here contain a group of a lists of WrappedWatchableObjects
         * We can get the group of lists with packet.getWatchableCollectionModifier();
         * Since the group usually only contains one list, we can get that first list with
         * packet.getWatchableCollectionModifier().read(0);
         *
         * All WrappedWatchableObjects have an index and a value. In this case the WrappedWatchableObject with an
         * index of 0 is responsible for making players look like they glow. We have to read this value and modify it
         * Since the WrappedWatchableObjects inside the list we found above are organized by index, we only need to read
         * the first WrappedWatchableObject in the list. The following line of code does this.
         */
        WrappedWatchableObject data = packet.getWatchableCollectionModifier().read(0).get(0);
        /*
         * However, since the packet might not contain WrappedWatchableObjects for every index, we don't know if the
         * first WrappedWatchableObject actually had an index of 0. We test to make sure it is correct below.
         */
        if (data.getIndex() != 0) {
            return;
        }
        /*
         * Now we can read the value of the WrappedWatchableObject with the next bit of code
         * This particular data should always be stored in byte form, but the assert statement is something that
         * will just throw an error in case I am wrong.
         */
        assert data.getValue() instanceof Byte;
        byte value = (byte) data.getValue();
        /*
         * Our goal is to make the player think that the entity is glowing
         * The particular byte we just stored contains information to describe certain things about the entity
         * The byte is composed of 8 bits, and each bit describes something different (See
         * https://wiki.vg/Entity_metadata#Entity and look under index 0 to see more info on what these values are)
         * In this case, bit 7 represents if an entity is glowing. We want to modify this bit and nothing else
         * The easiest way to do this is use the bitwise or operator. Doing 0x40 is a binary value with only the 7th
         * bit set, so doing "value | 0x40" just sets the 7th bit to 1, making it glowing.
         * Java also makes it an int by default, so we have to cast back to byte.
         */
        byte newValue = (byte) (value | 0x40);
        /*
         * We can use an object called a WrappedDataWatcher to modify the packet
         * We create one here
         */
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        /*
         * I'm not quite sure what this next line of code does
         * My best guess is that it makes sure that the packet properly encodes the new value as a byte
         */
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        /*
         * This adds the data into the watcher
         * It uses the serializer that we defined above
         */
        watcher.setObject(0, serializer, newValue);
        /*
         * This next line should correctly modify the packet
         * It applies the watcher to the packet
         */
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        // The packet should have been modified correctly, so we should be ready to go!
    }
}
