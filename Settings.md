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
        <td>attackingTeamSpawnLocation</td><td>-17 51 67</td>
        <td>
            The location attackers will be teleported to when the game is started. Needs
            to be set for the game to work.
        </td>
    </tr>
    <tr>
        <td>defendingTeamSpawnLocation</td><td>-67 51 69</td>
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
        <td>respawnLocation</td><td>-95 51 100</td>
        <td>
            The spawn point of each player will be set to this when the game starts.
            This means that killed players will respawn as silverfish here.
        </td>
    </tr>
    <tr>
        <td>lobbyLocation</td><td>-44 137 -86</td>
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
        <td>lobbyPasteLocation</td><td>-44 136 -86</td>
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
    <tr>
        <td>killArrows</td><td>true</td>
        <td>
            If true, then arrows (not including spectral arrows) will do lethal damage
            to anything they hit. This is a core characteristic of the game, but if you
            want to disable it you can.
        </td>
    </tr>
    <tr>
        <td>clearInventoryOnDeath</td><td>true</td>
        <td>
            If true, then players will not drop any items when they die, and the items will
            instead be deleted. This requires keepInventory to be false.
        </td>
    </tr>
    <tr>
        <td>protectedLayerEnabled</td><td>true</td>
        <td>
            If the protected layer is enabled, then all blocks with a y-value less than
            or equal to the protectedLayerLevel cannot be broken or blown up, except
            by players in creative mode. This is used to make the floor of the map
            unbreakable.
        </td>
    </tr>
    <tr>
        <td>protectedLayerLevel</td><td>50</td>
        <td>
            The top y-level that should be unable to be broken. Has no effect unless
            protectedLayerEnabled is set to true.
        </td>
    </tr>
    <tr>
        <td>disableEnderChests</td><td>true</td>
        <td>
            If true, right-clicking on an Ender Chest will have no effect. Prevents
            players from smuggling items between rounds.
        </td>
    </tr>
    <tr>
        <td>disableHunger</td><td>true</td>
        <td>
            If true, all players will permanently have max hunger.
        </td>
    </tr>
    <tr>
        <td>preventRemovingArmor</td><td>true</td>
        <td>
            If true, players cannot take off armor. Prevents players from taking
            off their colored leather armor.
        </td>
    </tr>
    <tr>
        <td>increaseEnvironmentalDamage</td><td>true</td>
        <td>
            If true, players will take increased damage from certain sources,
            such as falling and drowning, but not damage from players.
            Please see environmentalDamagePercentIncrease.
        </td>
    </tr>
    <tr>
        <td>environmentalDamagePercentIncrease</td><td>100</td>
        <td>
            If increaseEnvironmentalDamage is true, this is the percent by which to increase
            damage from certain sources. 100 means double damage, 50 means 1.5x damage, etc.
            This may be changed in the future to just be a direct multiplier instead of a percent.
            If increaseEnvironmentalDamage is false, this has no effect.
        </td>
    </tr>
    <tr>
        <td>beaconsRevealPlayers</td><td>true</td>
        <td>
            If true, when a beacon is broken, all players nearby (including the player who
            broke the beacon) will be given the glowing effect for a few seconds.
            This gives beacons an actual use and gives a way for players to find opponents.
        </td>
    </tr>
    <tr>
        <td>morphOnRespawn</td><td>true</td>
        <td>
            If true, players will respawn as a mostly harmless silverfish.
            This is basically the game's equivalent to spectator mode. If you disable
            this, it is recommended you enable hardcore mode in server.properties
            so that players will actually be eliminated when they are killed.
        </td>
    </tr>
    <tr>
        <td>disableMorphedPlayerDeathMessages</td><td>false</td>
        <td>
            If true, no death message will be displayed when players morphed as silverfish or
            other mobs die.
            If false, morphed players will have death messages, but the death messages will be
            changed to a different color to avoid distracting players.
        </td>
    </tr>
    <tr>
        <td>playersDropArrows</td><td>true</td>
        <td>
            If true, players will drop a few (currently 3) arrows when they die.
        </td>
    </tr>
    <tr>
        <td>announceBrokenTargets</td><td>true</td>
        <td>
            If true, a chat message will be sent to all players when a target is broken.
        </td>
    </tr>
    <tr>
        <td>enforcePrepTime</td><td>true</td>
        <td>
            If true, attackers will be unable to break blocks during the "prep" game phase
            where defenders have time to prepare. If false, prep time is basically
            an honor system.
        </td>
    </tr>
    <tr>
        <td>preventPrematureTargetDestruction</td><td>true</td>
        <td>
            If true, players (mainly defenders) will be unable to destroy objectives
            during prep time. This prevents defenders from destroying a target and then
            selecting it to prevent the attackers from ever breaking it.
        </td>
    </tr>
    <tr>
        <td>glowingTeammates</td><td>false</td>
        <td>
            If true, players on your team will appear to have the glowing effect. Requires
            ProtocolLib. This is very buggy right now so I recommend you leave this setting
            as false.
        </td>
    </tr>
    <tr>
        <td>timeInLobby</td><td>30</td>
        <td>
            If "/game auto" is on, then this setting specifies the number of seconds
            in the lobby before the next round starts.
        </td>
    </tr>
    <tr>
        <td>explosiveArrows</td><td>true</td>
        <td>
            If true, tipped arrows with the #FF0000 color will be explosive and create
            explosions when they hit something.
            Here is a command to get such an arrow:
            <code>
            /give @s tipped_arrow{CustomPotionColor:16711680,display:{Name:'["",{"text":"Explosive Arrow","italic":false}]'},HideFlags:32}
            </code>
        </td>
    </tr>
    <tr>
        <td>igniteCocktails</td><td>true</td>
        <td>
            If true, the custom cocktail item will be enabled. Any potion (not splash or
            lingering) with the rgb(131, 84, 50) color can be turned into a deadly weapon
            by left-clicking fire with it.
            <p>
                Here is a command to get such a cocktail:
                <code>
                    /give @p potion{CustomPotionColor:8606770,display:{Name:'["",{"text":"Cocktail","italic":false}]',Lore:['["",{"text":"Left-click fire with this item","italic":false}]','["",{"text":"to ignite the fuse. Throw it","italic":false}]','["",{"text":"quickly!","italic":false}]']},HideFlags:32}
                </code>
                This is too long for chat so it must be run in a command block.
            </p>
        </td>
    </tr>
    <tr>
        <td>portalSystemEnabled</td><td>true</td>
        <td>
            If true, then end portal frames will act as a navigation system for defenders.
            Defenders can use end portal frames to teleport to other placed end portal
            frames.
        </td>
    </tr>
</table>
