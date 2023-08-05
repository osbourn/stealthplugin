import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.osbourn.stealthplugin.StealthPlugin;
import org.bukkit.scoreboard.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommandTests {
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
    public void setupCommand() {
        PlayerMock player = server.addPlayer();

        assertTrue(server.dispatchCommand(player, "setup"));
        assertTrue(server.getScoreboardManager().getMainScoreboard().getTeams().isEmpty(),
                "No teams should be created when player does not have permissions");

        player.setOp(true);
        assertTrue(server.dispatchCommand(player, "setup"));

        assertEquals(2, server.getScoreboardManager().getMainScoreboard().getTeams().size(),
                "Two teams should have been created");

        Team redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("red");
        assertNotNull(redTeam);
        Team blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("blue");
        assertNotNull(blueTeam);
    }
}
