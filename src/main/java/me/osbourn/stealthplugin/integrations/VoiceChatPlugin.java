package me.osbourn.stealthplugin.integrations;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class VoiceChatPlugin implements VoicechatPlugin {
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
            System.out.println("error");
            return;
        }
        Player senderPlayer = (Player) senderConnection.getPlayer().getPlayer();
        for (Player recieverPlayer : Bukkit.getOnlinePlayers()) {
            VoicechatConnection connection = voicechatServerApi.getConnectionOf(recieverPlayer.getUniqueId());
            if (connection != null && !senderPlayer.equals(recieverPlayer) && onSameTeam(senderPlayer, recieverPlayer)) {
                voicechatServerApi.sendStaticSoundPacketTo(connection, event.getPacket().staticSoundPacketBuilder().build());
                System.out.println("Sent " + senderPlayer.getName() + " to " + recieverPlayer.getName());
            }
        }
    }

    private boolean onSameTeam(Player player1, Player player2) {
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Team team1 = scoreboard.getEntryTeam(player1.getName());
        Team team2 = scoreboard.getEntryTeam(player2.getName());
        return team1 != null && team1.equals(team2);
    }
}
