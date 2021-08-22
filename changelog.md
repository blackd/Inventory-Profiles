<!-- latest begin -->
### 1.0.1
- fixes situations where the hot bar becomes inaccessible when opening double chest.
- fixed enable picking up items in empty locked slots in single player on some platforms.

### 0.9.0
- Added equipment Profiles! You can now define sets of equipment and easily switch between them with a press of a button. Read more on how to set it up at [Profiles Documentation](https://inventory-profiles-next.github.io/profiles/)
- Added support for over stacked items like [Carpet's](https://github.com/gnembon/fabric-carpet) stacking of empty shulkers
- Fixed problem on Windows if the world name contains special characters.
- Fixed Continuous crafting. Now it's 100% reliable.
- All non-vanilla storages like Dank storage and Backpack will now be ignored by default.
- Removed all "teaks" that might be considered cheating. Like disabling lava fog and various cooldowns. We never made them work in 1.17.x anyway.
- Visit our new site [Inventory Profiles Next](https://inventory-profiles-next.github.io/) where you can find documentation about all the advanced features. 

### Known Issues in Forge 1.17.1 due to missing MixIn support 
- Preventing swipe moving of locked slots doesn't work.
- Preventing item pickups in empty locked slots falls back to the server method i.e. items are picked into the inventory and then thrown out.

<!-- latest end -->

<!-- rest begin -->

### 0.8.6
- Fixed issue with Forge mixin handling that causes incompatibility with [Timeless and Classics](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classic) and [I18nUpdateMod](https://www.curseforge.com/minecraft/mc-mods/i18nupdatemod), and possibly others

### 0.8.5
- Fixed problem in Forge mods that made sorting impossible if the inventory/container has more than 1 enchanted item with same enchants, same item type, and same name and items were arranged in specific way.
- Added hotkey for reloading the mod configurations. It's recommended that it's only used while defining new custom rules and kept unset in other situations.

### 0.8.4
- Fixes locked slots behaviour when changing dimensions/worlds. There is new config that allows the user to control how many client ticks need to pass before the locked slot keeper is reinitialised on dimension/world change. This wait is necessary because the inventory might be empty when the player is spawned in the new dimension/world and we need to wait to receive the inventory content from the server.

### 0.8.3
- Now on servers also items will not be picked into empty locked slots. NOTE! Doesn't work while inventory is open. It's not possible to distinguish between player moving items and server initiated changes.
- Added configuration to disable mod buttons on non-vanilla screens. For better compatibility with othe mods

### 0.8.2
- Added per server locked slots. Enabled by default. Can be disabled in the configuration.

### 0.8.1
- Added support for single click/press inventory throw. Hotkey can be configured in Hotkeys. Disabled by default. This obeys the same rules for hotbar as "move all". 
- Fixed Shift+Click Ctrl+Q skipping locked slots to work on servers. 
- Clarified, in the tooltip, that not picking up items in locked slots works only in single player. Support for servers will require service side mod.
- We now have a discord server **[![2][2]][1]**

### 0.8.0
- Shift+Click Ctrl+Q can now be disabled and enabled (default) for locked slots. This also applies for swipe variant of the actions.
- SINGLE PLAYER ONLY. Items will not be picked into empty locked slots

## Known issues
- When UI scaling is more than 2 or in case of tiny game windows the config screen might not show the anchor header correctly.
- If you make the game window 0x0 pixels while the configuration screen is open, sometimes the game crashes. 


[1]: https://discord.gg/23YCxmveUM
[2]: https://img.shields.io/discord/861171785897738240?label=Discord&logo=discord&style=plastic

<!-- rest end -->
