# HackControl
Version: 1.19.3 (spigot/paper)

The purpose of this plugin is making it easier to manage hack controls,
you can teleport players to a specific location and have a private chat,
then you can check if they are using cheats through third party tools.

## Features
- Put players in hack control mode
- Stop an hack control at any time
- Set an automatic ban for players who leave during an hack control
- Easily change the hack control locations using in-game commands
- Freezing players

| Command    | Description |
| ------------- |-------------|
| /control start [player] | Starts an hack control |
| /control end [player] | Ends an hack control |
| /control setup | Lets you set spawn positions for the hack controls |
| /control reload | Reloads the plugin configuration |
| /freeze [player] <true/false> | Freeze or unfreeze a player |
<br>

| Permission    | Description / Command |
| ------------- |-------------|
| hackcontrol.control | /control |
| hackcontrol.control.start | /control start |
| hackcontrol.control.end | /control end |
| hackcontrol.control.end.others | End hack controls of players <br>controlled by other staffers |
| hackcontrol.control.setup | /control setup |
| hackcontrol.control.reload | /control reload |
| hackcontrol.control.bypass | You cannot hack control this player |
| hackcontrol.freeze.bypass | You cannot freeze this player |
| hackcontrol.freeze | /freeze |
