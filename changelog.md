# Inventory Profiles Next v0.8.1

## What's new

- Added support for single click/press inventory throw. Hotkey can be configured in Hotkeys. Disabled by default. This obeys the same rules for hotbar as "move all". 
- Fixed Shift+Click Ctrl+Q skipping locked slots to work on servers. 
- Clarified, in the tooltip, that not picking up items in locked slots works only in single player. Support for servers will require service side mod.
- We now have a discord server **[![2][2]][1]**

## Known issues
- When UI scaling is more than 2 or in case of tiny game windows the config screen might not show the anchor header correctly.
- If you make the game window 0x0 pixels while the configuration screen is open, sometimes the game crashes. 

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
