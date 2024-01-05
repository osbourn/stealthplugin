# Settings

This is a list of settings/config options available. This list might be outdated, so please
open an issue if you see something incorrect. It also isn't sorted by anything right now, which
I might change in the future.

<table>
    <tr><th>Setting Name</th><th>Default Value</th><th>Description</th></tr>
    <tr>
        <td>timePerRound</td><td>300</td>
        <td>
            The number of seconds on the timer when the round starts.
        </td>
    </tr>
    <tr>
        <td>prepTime</td><td>30</td>
        <td>
            The number of seconds in the "prep" game phase where attackers can't break blocks and
            defenders have time to prepare.
        </td>
    </tr>
    <tr>
        <td>displayGameTargetsOnScoreboard</td><td>true</td>
        <td>
            Whether the names of the game targets and whether they have been destroyed appear on
            the scoreboard. This should be left as true unless you have some other way for the
            attackers to learn of the objectives.
        </td>
    </tr>
    <tr>
        <td>displayTeamsOnScoreboard</td><td>false</td>
        <td>
            Whether the teams and the number of surviving players should appear on the scoreboard.
            This is generally redundant with displayPlayerNamesOnScoreboard, so it is false by default, but this should be enabled if you run out of space and have to disable
            displayPlayerNamesOnScoreboard.
        </td>
    </tr>
    <tr>
        <td>displayPlayerNamesOnScoreboard</td><td>true</td>
        <td>
            Whether each player should be displayed on the scoreboard individually. This might
            overflow the scoreboard if you have a large number of players and/or objectives.
            If so, you can disable this option and enable displayTeamsOnScoreboards.
        </td>
    </tr>
    <tr>
        <td>displayTimeOnScoreboard</td><td>true</td>
        <td>
            Whether the amount of time remaining should be displayed on the scoreboard. This should
            generally be kept as true.
        </td>
    </tr>
    <tr>
        <td>displayScoreOnScoreboard</td><td>false</td>
        <td>
            Whether the score (the number of rounds won by each team) should be displayed on the
            scoreboard. The score is displayed after each round and generally isn't important
            during a round, so this is disabled by default to save scoreboard space.
        </td>
    </tr>
    <tr>
        <td>applyInvisibilityOnStart</td><td>false</td>
        <td>
            If true, invisibility will be given to players when the game starts. This will prevent
            players from seeing nametags, but they can still see armor.
            Because nametags are disabled by default if the plugin is set up properly, this option
            should only ever be set to true if there is a bug in the plugin which allows players
            to see nametags.
        </td>
    </tr>
    <tr>
        <td>attackingTeamName</td><td>red</td>
        <td>
            This is the name of the team that is designated as the attackers. This option will
            change automatically between red and blue when roles are swapped.
            In theory, this could be changed to another team other than red or blue if you set
            it up manually, but the plugin has only ever been tested on the red and blue teams
            so it might not work.
        </td>
    </tr>
    <tr>
        <td>defendingTeamName</td><td>blue</td>
        <td>
            Like the attackingTeamName, this is the name of the team designated as the
            defenders and will change automatically when roles are swapped.
        </td>
    </tr>
    <tr>
        <td>attackingTeamSpawnLocation</td><td>unset</td>
        <td>
            The location attackers will be teleported to when the game is started. Needs
            to be set for the game to work.
        </td>
    </tr>
    <tr>
        <td>defendingTeamSpawnLocation</td><td>unset</td>
        <td>
            The location defenders will be teleported to when the game is started. Needs
            to be set for the game to work.
        </td>
    </tr>
    <tr>
        <td>attackingTeamChestLocation</td><td>unset</td>
        <td>
            If set to the location of a chest, all items in that chest will be given to each
            attacker when the game starts. Because of the kit system, this option is generally
            not needed, but if you want to add a particular item to every attacking kit you
            can choose to set this option instead.
        </td>
    </tr>
    <tr>
        <td>defendingTeamChestLocation</td><td>unset</td>
        <td>
            If set to the location of a chest, all items in that chest will be given to each
            defender when the game starts.
        </td>
    </tr>
    <tr>
        <td>respawnLocation</td><td>unset</td>
        <td>
            The spawn point of each player will be set to this when the game starts.
            This means that killed players will respawn as silverfish here.
        </td>
    </tr>
    <tr>
        <td>lobbyLocation</td><td>unset</td>
        <td>
            The location that players will teleport to when the "/game tolobby" command is run,
            or whenever players are to be sent to the lobby.
        </td>
    </tr>
    <tr>
        <td>numberOfTargets</td><td>2</td>
        <td>
            The number of different targets that the attackers have to break in order to win.
            You may also want to look into the /gameobjectives command if you want to adjust
            the list of available targets.
        </td>
    </tr>
    <tr>
        <td>morphedPlayersCanAttack</td><td>false</td>
        <td>
            If false, players morphed as silverfish (or any other mob) will not be able to attack
            anything. This should be left as false unless you want silverfish players punching
            living players.
        </td>
    </tr>
    <tr>
        <td>morphedPlayersIgnoreArrows</td><td>true</td>
        <td>
            If true, then arrows will pass through morphed/dead players instead of hitting them.
            Prevents dead players from blocking shots.
        </td>
    </tr>
    <tr>
        <td>mapPasteFile</td><td>internal/map.schem</td>
        <td>
            The location of the map schematic file, relative to the plugin/stealthplugin folder.
            You can change this option if you would like to use a custom map.
            If this option starts with "internal/" it will use a schematic stored directly
            in the plugin jar.
        </td>
    </tr>
    <tr>
        <td>mapPasteLocation</td><td>0 50 0</td>
        <td>
            The coordinates where the map is pasted, such as with the /pastestructure command.
            If you change this setting, you should probably adjust the
            attackingTeamSpawnLocation, defendingTeamSpawnLocation, and respawnLocation
            settings as well.
        </td>
    </tr>
    <tr>
        <td>lobbyPasteFile</td><td>internal/lobby.schem</td>
        <td>
            Like mapPasteFile, this option specifies which schematic file to use when pasting
            the lobby with "/pastestructure lobby". You might not need to set this if
            you plan on pasting the lobby manually.
        </td>
    </tr>
    <tr>
        <td>lobbyPasteLocation</td><td>unset</td>
        <td>
            Like mapPasteLocation, this specifies where the lobby is pasted to when
            "/pastestructure lobby" is run.
        </td>
    </tr>
    <tr>
        <td>killEntitiesBeforePaste</td><td>true</td>
        <td>
            If true, all non-player entities will be deleted when the map is reset.
            This helps clear up mobs and other entities that might be left over from
            the most recent round.
        </td>
    </tr>
</table>
killArrows: true
clearInventoryOnDeath: true
protectedLayerEnabled: true
protectedLayerLevel: 50
disableEnderChests: true
disableHunger: true
preventRemovingArmor: true
increaseEnvironmentalDamage: true
beaconsRevealPlayers: true
morphOnRespawn: true
playersDropArrows: true
announceBrokenTargets: true
enforcePrepTime: true
preventPrematureTargetDestruction: true
glowingTeammates: false
timeInLobby: 30
explosiveArrows: true
igniteCocktails: true
portalSystemEnabled: true
