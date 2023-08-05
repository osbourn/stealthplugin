import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.osbourn.stealthplugin.StealthPlugin;
import org.bukkit.Material;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandTests {
    private ServerMock server;
    private StealthPlugin plugin;
    private PlayerMock opPlayer;
    private PlayerMock nonOpPlayer;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(StealthPlugin.class);
        opPlayer = server.addPlayer();
        opPlayer.setOp(true);
        nonOpPlayer = server.addPlayer();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void setupCommandNonOp() {
        assertTrue(nonOpPlayer.performCommand("setup"));
        assertTrue(server.getScoreboardManager().getMainScoreboard().getTeams().isEmpty(),
                "No teams should be created when player does not have permissions");
    }

    @Test
    public void setupCommand() {
        assertTrue(opPlayer.performCommand("setup"));

        assertEquals(2, server.getScoreboardManager().getMainScoreboard().getTeams().size(),
                "Two teams should have been created");

        Team redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("red");
        assertNotNull(redTeam);
        Team blueTeam = server.getScoreboardManager().getMainScoreboard().getTeam("blue");
        assertNotNull(blueTeam);
    }

    @Test
    public void giveTeamArmorCommand() {
        opPlayer.performCommand("setup");
        Team redTeam = server.getScoreboardManager().getMainScoreboard().getTeam("red");

        PlayerMock player1 = server.addPlayer();
        PlayerMock player2 = server.addPlayer();
        redTeam.addEntry(player1.getName());

        assertTrue(opPlayer.performCommand("giveteamarmor"));

        assertEquals(Material.LEATHER_HELMET, player1.getEquipment().getHelmet().getType());
        assertEquals(Material.LEATHER_CHESTPLATE, player1.getEquipment().getChestplate().getType());
        assertEquals(Material.LEATHER_LEGGINGS, player1.getEquipment().getLeggings().getType());
        assertEquals(Material.LEATHER_BOOTS, player1.getEquipment().getBoots().getType());

        assertNull(player2.getEquipment().getHelmet());
        assertNull(player2.getEquipment().getChestplate());
        assertNull(player2.getEquipment().getLeggings());
        assertNull(player2.getEquipment().getBoots());
    }

    @Test
    public void togglesbCommand() {
        // For some reason this doesn't work with non-op players even though I think it does in-game
        assertEquals(server.getScoreboardManager().getMainScoreboard(), opPlayer.getScoreboard());
        assertTrue(opPlayer.performCommand("togglesb"));
        assertNotEquals(server.getScoreboardManager().getMainScoreboard(), opPlayer.getScoreboard());
        assertTrue(opPlayer.performCommand("togglesb"));
        assertEquals(server.getScoreboardManager().getMainScoreboard(), opPlayer.getScoreboard());
    }
}
