# Inventory Profiles Next 

## What's new

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

## Get in touch **[![2][2]][1]**


## Migration from old Inventory Profiles

    move MINECRAFTHOME/config/inventoryprofiles somewhere.
    remove the old mod.
    install the this mod.
    move back inventoryprofiles folder from above.
    rename the folder to inventoryprofilesnext.

## Requirements

- Fabric loader >=0.11.6

- [fabric api](https://modrinth.com/mod/fabric-api) 
  
- [ModMenu](https://modrinth.com/mod/modmenu)

- Forge 36.1.32

[1]: https://discord.gg/23YCxmveUM
[2]: https://img.shields.io/discord/861171785897738240?label=Discord&logo=discord&style=plastic
