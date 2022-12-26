<!-- latest begin -->

### 1.9.1

- Added translation for Chinese Traditional thanks to 午夜的大叔
- Fixed hot bar locked slots rendering while in spectator mode.
- Extended mod compatibility

#### Supported Minecraft versions
- **1.16.x**
- **1.18.2**
- **1.19[.1-2]**
- **1.19.3**


<!-- latest end -->

<!-- rest begin -->

### 1.9.0

- added support for 1.19.3 forge
- finalised support 1.19.3 for fabric
- fixed toggle settings not working when more then one mod uses libIPN


### 1.8.6

- added Korean translation thanks to @ssolephant.
- added support for 1.19.3-rc1 and probably 1.19.3 proper.



### 1.8.5

- fixes crash when in creative mode middle click is used to pick block and the inventory and hotbar are full of other blocks
- fixes incompatibility/crash with mods that check for free slots in the hotbar
- 
### 1.8.4

- fixed single player hotbar whitelist
- now depends on libIPN v1.0.5


### 1.8.3

- Auto refill can now be configured to use any available food when refilling food slots.

  **NOTE:** The best available food is chosen i.e. if you have a `notch apple` it will be next :)
- Added workaround for [Forge bug](https://github.com/MinecraftForge/MinecraftForge/issues/9088) that crashes the game if there is a missing dependency and mixin resolution failed.


### 1.8.2

- fixed/added support for fishing rods auto refill.
- update Ukrainian translation. Thanks to BurrConnie

### 1.8.1

- fixed mouse wheel events being processed twice in some cases.

### 1.8.0

- Introducing [libIPN](https://www.curseforge.com/minecraft/mc-mods/libipn). The GUI/Config code is now in separate library mod. [libIPN](https://www.curseforge.com/minecraft/mc-mods/libipn) will be used by my other mods. And others?... If there are other brave devs.
- **Inventory Profiles Next** now depends on [libIPN](https://www.curseforge.com/minecraft/mc-mods/libipn). This means you will need to install [libIPN](https://www.curseforge.com/minecraft/mc-mods/libipn) too or **Inventory Profiles Next** will not work
- Added Ukrainian translation. Thanks to **ttrafford7**


### 1.7.2

- Continuous/Auto Crafting now supports the Stone Cutter.

<iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/ENpE05awR38?wmode=transparent" height="358" width="638"></iframe>


#### NOTE

Switched to modern Kotlin support. This means that this mod now requires [Kotlin for Forge](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge) or [Fabric Language Kotlin](https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin)



### 1.7.1

- fixed fabric 1.16.x build to support Java 8.
- Most boolean settings can now be assigned hot key to toggle them in game. GUI settings require reopen of the current GUI.
- Config files created by versions prior 1.1.9 are no longer fully supported. If you are upgrading from v 1.1.8 or lower please first install v1.7.0 start the game once to upgrade the config and then upgrade to 1.7.1
- Locked slots enable/disable now has effect in single player too.
- Fixed modpack arena generation.
- Auto refill now takes NBT and Custom Names into account. For all the fake items on servers :)
- Added blacklist for items going in the inventory. By default, "carryon" mod is blacklisted.
- The configuration file created by v 1.7.1 is not backward compatible.

### 1.7.0

- Added item scrolling. Mouse wheel up moves from player inventory to the open container. Mouse wheel down does the opposite.
- Move all/matching can now just top up already existing stacks. By default, it's activated by holding `caps lock` while clicking the button on pressing the hotkey.
- Added new button to the overlay for yet another way to open the mod settings. 80% of the questions would be solved if you know how to show the config.
- Hoppers are now treated like non sortable storage.

<iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/XK_GO2LQt58?wmode=transparent" height="358" width="638"></iframe>


### 1.6.5

- fixed a crash when another mod puts items into the hotbar during world generation before the game has started.


### 1.6.4

- fixed item select with middle click when pickup to inventory is active
- fixed mainhand/offhand swap for locked slots and when pickup to inventory is active
- fixed single player not being able to pickup items in the hotbar at all
- removed `Add Inventory Buttons to All In Game Screens` from `Gui Settings` It was a temporary workaround for the then missing GUI Hints Manager.


### 1.6.2 and 1.6.3

- Picked up items will now go directly into the inventory instead of taking hotbar space. This can be changed in the `Locked Slots` tab of the config.
- Added localisation for Português Brasileiro

<iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/zj1DzMvXO9Q?wmode=transparent" height="358" width="638"></iframe>


### 1.6.1

- Auto refill now supports buckets and honey bottles
- Swipe move/throw now can be configured on per UI bases 
- Forge 1.17.x build now depends on kotlin for forge >=2
- on 1.14, 1.15, 1.16 the default value of "Number of Ticks to Wait Before Auto Refill" is now 1 and setting it to 0 is no longer possible.

<iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/eLYY9yKMvAY?wmode=transparent" height="358" width="638"></iframe>


### 1.6.0

- fixed crash on Forge 1.19
- fixes gui incompatibility with [CC:Tweaked](https://www.curseforge.com/minecraft/mc-mods/cc-tweaked)
- switched to modern Kotlin support. This means that this mod now requires [Kotlin for Forge](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge) or [Fabric Language Kotlin](https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin)


### 1.5.3
- Added highlighting of all items of type:
  <iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/ndef7aenLWg?wmode=transparent" height="358" width="638"></iframe>

### 1.5.2
- Added interoperability API for use by hotbar swap mods. First mod to use it will probably be https://www.curseforge.com/minecraft/mc-mods/hotbarcycle

### 1.5.1
- Added support for named items in profiles. 

  Although the example below uses netherite pickaxe we don't really recommend it. Names would be useful for items like backpacks or even shulker boxes or other type of items where there is no other meaningful way to distinguish between them.

```
profile NamedTest
	HOT3
		"minecraft:netherite_pickaxe"("My Awsome Name") -> "Enchantments" : [{id:"minecraft:fortune",lvl:3}]

```

- config folder structure have been changed, now all files related to a server/world are located in a folder named after the server/world. All old configurations will be updated. However, if you downgrade to old version the configurations will need to be moved manually.


### 1.5.0

- License was changed to AGPL-3. This was made, so it will be license violation, if in future OverWolf/CurseForge decide to fully cut off third party launchers.
- There are now about 740 other mod UIs that have custom button positioning.
- Extended the Modpack Dev tools (see the video). 
  We now offer a tool that will create a platform containing all blocks that we have detected to open a UI.
  Modpack Devs can use this to check if there are un/supported UIs and use the editor to move the buttons.
  Also the player will get all the items that potentially open a UI.
  The tool doesn't catch every possible UI, but is a good start.
  <iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/STZoofsZBtI?wmode=transparent" height="358" width="638"></iframe>
  - **NOTE:** this will work only single player with cheats on, or MAY BE on servers also if you have `/op` or `/give`, `/setblock` and `/fill` permissions.
  - **WARNING:** depending on the modpack and your PC it's highly probably that it will crash the client *AND/OR* the server.

### 1.4.0

- Added support for wildcards in custom sort rules. See the [wiki](https://inventory-profiles-next.github.io/custom-rules/add/#wildcards) for more information.
- Added new setting "Continuous Crafting Method" that allows you to choose between 2 modes of continuous/auto crafting. If the default doesn't work for you try the alternative.
- Workaround for [MC-216434](https://bugs.mojang.com/browse/MC-216434) when continuous/auto crafting is enabled. Next release will contain fix for it independent of continuous crafting.
- Made continuous/auto crafting more stable...


### 1.3.7/8

- New release because the last one was bodged by GihHub Action resulting invalid jar files. First forge builds had problems then some fabric builds are not working for some players. As result github actions will no longer be used for releasing.
- Added configuration "Wait Ticks Before Next Craft Click" that can be used to make auto-crafting more stable on slow servers.
- Now auto/continuous crafting ignores items durability. As in auto-crafting of dispensers with farmed bows is now possible.
  <iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/BOVKOCVPIGY?wmode=transparent" height="358" width="638"></iframe>
- Move/Throw is now configured to ignore durability. There is a new setting "Ignore item durability for Throw/Move" that can be used to revert to the old behaviour.
  <iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/NFpmpHZA0kw?wmode=transparent" height="358" width="638"></iframe>
- Move can now move only the items of the type under the cursor despite the type being present in the target chest/inventory.
  <iframe allowfullscreen="allowfullscreen" src="https://www.youtube.com/embed/GPsqasYC9Bg?wmode=transparent" height="358" width="638"></iframe>


### 1.3.6
- Addressed a very rare issue that causes random game crash due to some mods changing the game startup sequence.
- fixed bug in 1.15.2 that prevented gui hints from working.

## Warning.
**Due to changes in 1.18.2 existing custom sort rules that use Tags might behave differently or not work at all.
You may need to update them to reflect any changes in the Tags.**


### 1.3.5
- Fixed infinite loop when no overlay buttons are visible and the editor is shown.
- Relaxed the rules so Profiles can support invalid namespaces that contain dashes.
- 1.18.2 is considered stable now with the above **warning** in mind.






### 1.3.4
- Fixed Auto Refill alert sounds on Forge when connected to Forge server.
- Fixes Auto Refill causing exception storm in the logs when connected to Forge server.
- Shiny new Russian translation thanks to @DrHesperus

### 1.3.3
- Fixed Hotkey assignment on Forge
- Added hints UIs from ~80 mods


### 1.3.2
- Added blacklist for Auto Refill
- Some fixes in the Hints system and edit UI

### 1.3.1
- Fixed "button locations not saving in some cases"

### 1.3.0
- Added in game Overlay Editor. If IPN buttons overlap with another mod UI you can now move them. Also allows you to influence how IPN detects the UI, for example as a chest to just as player inventory.
- Fixed Profiles switching with hotkeys.
- Fixed Auto Refill clashing with Profiles switching.
- Added new setting to allow Auto Refill to use items from locked slots.


### 1.2.4
- improved compatibility with Inventorio, now sorting works in the extended inventory and the buttons are not overlaping the extra slots.
- added visual and sound alerts when tools run out of durability and are replaced or when there is no replacement tool. Check the "Auto Refill" configuration for more information.
- All Realms worlds will now share configurations for locked slots and profiles. It's impossible to reliably detect the Realms world.


### 1.2.3
- workaround for REI tooltip rendering incompatibility.

### 1.2.2 - just a hotfix
- fixed typo.
- removed a debug dump that looked like it's something important.
- added forge 1.15.2 version

### 1.2.1
- fixes throw matching/all not dropping all expected items.
- "fixed" handling of overstacked items. Now we will just leave them in place.
- forge 1.18 support looks stable enough
- fabric 1.14 gets an update because of the first fix. If no other major problems are found this will be the last update.


### 1.2.0
- added support for Loyalty enchanted items. Throwing a loyalty trident will not trigger refill to allow the thrown one to return to the same slot.
- new auto refill feature. You can now allow non-enchanted items to break if their max durability is below a preset value.
- native 1.18 version.

### 1.1.9
- fixed locked slots behaviour when a chest or other container is opened and swipe drop/move is used.
- native 1.18 version.

# 1.14 END OF LIVE ANNOUNCEMENT

1.14 gets an update because I don't want to leave it with a buggy major feature.
This is the last release of Inventory Profiles Next for Minecraft 1.14.x

### 1.1.8
- it is now possible to disable all user actions for locked slots. It becomes very practical when used in conjunction with "Quick Disable" hotkey.
- fixed locked slots behaviour when a chest or other container is opened.
- rearranged the config screen. Now "Locked Slots" and "Auto Refill" have their own pages. However, the settings to enable or disable them are still in "Mod Settings"
- the configuration file has changed. The old one will automatically be converted but if you downgrade all settings from "Locked Slots" and "Auto Refill" will be lost.


### 1.1.7
- fixed crash when opening the wiki link and config folder.
- fixes incompatibility with MaliLib where sometimes this mod key bindings will stop working.
- fixes the version checker which didn't display the new version message.

### 1.1.6
- fixed crash on 1.17.
- 1.18 is now separate build and 1.17 builds are no longer compatible with 1.18

### 1.1.5
- the hotbar now shows the locked slots.
- new setting (enabled by default) to disable drop for locked slots containing non-stackable items.
  prevents accidental drops of gear.
- fixed "move all" when key binding is set to "ctrl + alt + q"
- some changes under the hood
- added updates check. The check is done once you enter a word/connect ot a server and message is displayed if there is new version available.

<!-- rest end -->

### 1.1.4
- added 1.15+ and 1.14.1+ versions. Just to be on par with the old mod
- the fabric 1.16 version now runs on all minor versions

### 1.1.3
- fixed java 8 incompatibility for 1.16.5 builds

### 1.1.2
- brand new Simplified Chinese translation thanks to @PVWXX
- profiles now support empty slots. For example in one profile you have shield in the offhend slot and another profile want it empty. For items moved out in this case an attempt will be made to move them in a free locked slot.<br>
  Just add the slot name without items in the configuration. 
- added full auto crafting. Just hold shift+alt while clicking on the crafting slot.
- fixed sort in column and row buttons to. They were swapped.
- fixed forge server crash.
- fixed forge client complaining if IPN is not installed on the server. 
- made handling of ignored screens and inventories more robust.

### 1.1.1
- fixed client crash on Forge 1.17.x
- fixed ignored container types handling
- probably fixed server crash on forge when the mod is installed. **NOTE:** Don't install it on servers it doesn't do anything.


### 1.1.0
* Added API for integration with other mods. It is now possible for other mods to:
  * mark their screens to be ignored so IPN won't try to interact with them.
  * give hints to IPN where to put its UI.
  * there is a configuration file that can provide the above information if the for some reason it's not possible for the other mod to provide in via the API. Entries in the default configuration file will be accepted in case official refusal from the other mod to provide integration or if the other mod is abandoned.

### 1.0.3
- fixes a problem where, on some servers, items were moved out of the locked slots on dimension change.

### 1.0.2
- fixes forge 1.17.1 mixin support

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
