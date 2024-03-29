import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.osbourn.stealthplugin.StealthPlugin;
import org.bukkit.scoreboard.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StealthPluginTests {
    private ServerMock server;
    private StealthPlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(StealthPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void mockPlugin() {
        // Empty method, for the purpose of testing the setUp and tearDown methods
    }

    @Test
    public void disconnectAndReconnect() {
        PlayerMock player = server.addPlayer();
        player.disconnect();
        player.reconnect();
    }
}
