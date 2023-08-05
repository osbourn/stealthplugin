package me.osbourn.stealthplugin.integrations;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

/**
 * This class keeps references to ProtocolLib out of the rest of the code, so that the plugin doesn't crash when
 * ProtocolLib is not installed
 */
public class ProtocolIntegration {
    public final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public void setup(GlowEffectManager glowEffectManager) {
        this.protocolManager.addPacketListener(glowEffectManager);
    }
}
