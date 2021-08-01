<div class="center" align="center">

# Inventory Profiles Next

An inventory sorter, with linear, horizontal and vertical sorting, and profiles support

![](https://i.imgur.com/e4CagVq.jpeg)

[Join us on Discord](https://discord.gg/23YCxmveUM)

</div>

## What's new
### 0.8.5
- Fixed problem in Forge mods that made sorting impossible if the inventory/container has more than 1 enchanted item with same enchants, same item type, and same name and items were arranged in specific way.
- Added hotkey for reloading the mod configurations. It's recommended that it's only used while defining new custom rules and kept unset in other situations.
### Known Issues in Forge 1.17.1 due to missing MixIn support
- Preventing swipe moving of locked slots doesn't work.
- Preventing item pickups in empty locked slots falls back to the server method i.e. items are picked into the inventory and then thrown out.

### Full change log
<div class="spoiler">

</div>
## Requirements

- Forge 1.16.5 >= 36.1.32
- Forge 1.17.x >= 37.0.17

<span></span>

- Fabric Loader >= 0.11.6 (see [Updating Fabric](#updating-fabric))
- Mod Menu >= 2.0.2

## Migrate from Inventory Profiles

1. Move <span class="red"><strong>MINECRAFTHOME/config/inventoryprofiles</strong></span> somewhere
2. Remove the old mod
3. Install this mod
4. Move back the <span class="red"><strong>inventoryprofiles</strong></span> folder from above
5. Rename it to <span class="green"><strong>inventoryprofilesnext</strong></span>

Note: If you're a Windows user, your **MINECRAFTHOME** is probably **%appdata%/.minecraft**

## Updating Fabric

For the official launcher make sure you don't have the game or the launcher running and follow [this guide](https://fabricmc.net/wiki/player:tutorials:install_mcl:windows)

For the other launchers... really you should know how to do it :)

## Known issues

- Anchor header may not be shown correctly if the UI scaling is more than 2 or if the game windows is too small

## Contributing

If something doesn't work, please file an issue [here](https://github.com/blackd/Inventory-Profiles/issues)

If you speak another language and would like to help to translate it, you can do so [here](https://github.com/blackd/Inventory-Profiles/tree/all-in-one/common/src/main/resources/assets/inventoryprofilesnext/lang)

If you're a Kotlin developer and want to contribute, please see [the source](https://github.com/blackd/Inventory-Profiles)

## Thanks

Thanks to **jsminda**, the original author of [Inventory Profile](https://github.com/jsnimda/Inventory-Profiles)

## License

This mod is distributed under [MIT license](https://github.com/blackd/Inventory-Profiles/blob/all-in-one/LICENSE)
