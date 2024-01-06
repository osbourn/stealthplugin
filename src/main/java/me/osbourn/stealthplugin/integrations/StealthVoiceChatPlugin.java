package me.osbourn.stealthplugin.integrations;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class StealthVoiceChatPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "stealthplugin";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }

    @Override
    public void initialize(VoicechatApi api) {

    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        // This code is similar to the code from here:
        // https://github.com/Flaton1/walkie-talkie-mod/blob/1.20.1/common/src/main/java/fr/flaton/walkietalkie/WalkieTalkieVoiceChatPlugin.java
        VoicechatServerApi voicechatServerApi = event.getVoicechat();
        var senderConnection = event.getSenderConnection();
        if (senderConnection == null) {
            return;
        }
        Player senderPlayer = (Player) senderConnection.getPlayer().getPlayer();
        if (holdingWalkieTalkie(senderPlayer)) {
            for (Player recieverPlayer : Bukkit.getOnlinePlayers()) {
                VoicechatConnection connection = voicechatServerApi.getConnectionOf(recieverPlayer.getUniqueId());
                if (connection != null && !senderPlayer.equals(recieverPlayer) && onSameTeam(senderPlayer, recieverPlayer)) {
                    voicechatServerApi.sendStaticSoundPacketTo(connection, event.getPacket().staticSoundPacketBuilder().build());
                }
            }
        }
    }

    private boolean onSameTeam(Player player1, Player player2) {
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Team team1 = scoreboard.getEntryTeam(player1.getName());
        Team team2 = scoreboard.getEntryTeam(player2.getName());
        return team1 != null && team1.equals(team2);
    }

    private boolean holdingWalkieTalkie(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        return mainHandItem.getType() == Material.IRON_INGOT || offHandItem.getType() == Material.IRON_INGOT;
    }
}
