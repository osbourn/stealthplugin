package me.osbourn.stealthplugin.integrations;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleVoiceChatIntegration {
    public void setup(JavaPlugin plugin) {
        BukkitVoicechatService service = plugin.getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            service.registerPlugin(new StealthVoiceChatPlugin());
        }
    }
}
