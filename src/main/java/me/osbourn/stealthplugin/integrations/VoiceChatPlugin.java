package me.osbourn.stealthplugin.integrations;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;

public class VoiceChatPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "stealthplugin";
    }

    @Override
    public void initialize(VoicechatApi api) {

    }
}
