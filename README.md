# Minecraft Stealth Minigame Plugin

This is a Spigot/Paper Plugin which runs a stealth-themed Minecraft minigame.

- [Requirements](#requirements)
- [Setup](#setup)
  - [Recommended changes to server.properties](#recommended-changes-to-serverproperties)
  - [Loading into a world](#loading-into-a-world)
  - [Run these commands](#run-these-commands)
- [Usage](#usage)

## Requirements

- Spigot or Paper for Minecraft 1.18.2 or higher.
(This was primarily tested on Paper 1.18.2., so paper is preferred over Spigot.
It uses blocks from 1.18 so earlier versions aren't possible, but feel free to contact me for
specific version requests.)
- WorldEdit 7.2.0 or higher. Used to paste the map included in the plugin.
- (Optional) ProtocolLib 4.8.0 or higher. Used for certain extra features, like glowing teammates
  (this is not working very well right now, so feel free to skip this for the time being).

## Setup

Install as normal by dragging the plugin jar into your `plugins` folder, along with WorldEdit.

### Recommended changes to server.properties

Before creating a world, it is recommended that you set the following options in `server.properties`.
None of these are required, but please read why they are recommended below.
```
spawn-protection=0
level-type=flat
generator-settings={"layers"\:[{"block"\:"minecraft\:air","height"\:1}],"biome"\:"minecraft\:the_void"}
enable-command-block=true
```

The first option disables spawn protection, which normally prevents players without operator permissions
from placing or breaking blocks near spawn. If your players complain that they can't break anything in
the map, but you (as an operator) can, this setting might be nonzero. If you can't change this for whatever
reason, change the location of the map with `/settings mapPasteLocation ~ ~ ~`.

The second and third options make Minecraft generate a void superflat world. This is preferable because
you are probably using only the map, but it should work fine on a vanilla world if it need be.

The last option enables command blocks. The actual game does not use command blocks in any way, so you can
keep this off if you have concerns about security, but command blocks are used for a minigame in the lobby,
and possibly a few more things in the future. But the game runs just fine with this option set to false.

You may also wish to consider making Adventure mode the default gamemode and turning up the difficulty
to something harder.

### Loading into a world

If you chose to create a void world, as specified above, you will probably die as soon as you load in.
OP yourself using the console, then use F3+F4 to switch your gamemode to creative so you don't fall into
the void. You may have turned into a silverfish when you died. If so, run `/unmorph` to switch back, or
`/unmorph *` to switch all players back. For more info on this, please see the `morphOnRespawn` setting.

### Run these commands

```
/setup
/pastestructure map
/pastestructure lobby
/time set midnight
```

The `/setup` command creates the teams, uses them to disable nametags, and sets multiple gamerules
to be more suitable for gameplay. You might have to run this command every time you launch the server
if Spigot is deleting empty teams.

`/pastestructure map` creates the map by pasting it from a schematic stored in the plugin. When running
this command, the game may lag for up to a minute because of the massive size of the map.

Likewise, `/pastestructure lobby` creates the lobby. You may need to select its location using
`/settings lobbyPasteLocation ~ ~ ~`.

I think the game is more fun during midnight, which is why I included the last command. :)

## Usage

Here is a quick overview of the most commonly used commands, which run the game:
- `/randomizeteams red:4 blue:3` - Randomly assign 4 players to the red team and 3 to the blue team.
- `/team join red Steve` - Assign Steve to the red team. This is a vanilla Minecraft command.
- `/pastestructure` - Reset the map
- `/game start` - Start the game. All participating players must be assigned to either the red or blue
team before running this command.
- `/game forcestart` - Start the game. If the game is already running, restart it.
- `/game tolobby` - Run this command after each round to send players back to the lobby.
- `/swaproles` - Switch which team is attacking and defending.
- `/game readynext` - Equivalent to running `/game tolobby`, then `/swaproles`, then `/pastestructure`.
- `/score reset` - Sets the score to "0 - 0"
- `/game auto on` - This will essentially run `/game start` and `/game readynext` for you on a schedule.
This generally won't go into effect until after a game has ended, so you still need to start the first
game.
