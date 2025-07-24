## Allows players to customize their home world's trash bin.

This cleaning plugin operates within a specific world (the player's home) and, when cleaning items, gathers them into a trash bin that the player has personally set up in their home. It combines various features, supports Folia, boasts highly configurable settings, and is compatible with multiple languages.

**Now, here's a hellish joke for you:**\
**Player** : Hey, my quantum solar panels got swept away by the cleaning plugin. Can you compensate me?\
**OP** : No way, you can't prove it, and my cleaning plugin doesn't keep records.\
**Player** : I just bought them!\
**OP** : What if you hid them, and I compensate you, essentially doubling them?\
**Player** : ***\
**OP** : Even if there were public trash bins on the server, someone would snatch up those solar panels.\
**OP** : What should I do?

And there you have it, this plugin was born.

When Foila supports multiple worlds, this cleaning plugin will benefit everyone

### Features:
---
- **Player-Created World Trash Bin:**\
Players have the ability to create a trash bin in their world.
Similar to the mechanics of previous sign-locked chests, the font on the sign will undergo a 
transformation after creation.
When the cleaning process is initiated, items will be automatically added to the home trash bin.
Configurable settings allow players to specify detection names and transformed names after cleaning.
\
![REPLACE ME WITH YOUR ALT TEXT](https://cdn.modrinth.com/data/nILG1dtG/images/69ae92502becde7a7a2f033815fa420c28cb6957.gif)

- **Support for Multiple Home Trash Bins:**\
Players can use commands to grant special players the ability to place additional home trash bins.
Default quantities for home trash bins are configurable.\
![REPLACE ME WITH YOUR ALT TEXT](https://cdn.modrinth.com/data/nILG1dtG/images/609be599004c03b52ec244c68437bd33b3de0f9d.gif)
- **Home Trash Bin Overflow to Public Trash Bin:**\
Items from home trash bins overflow into the public trash bin.
Public trash bins can have a configurable maximum page count and a blacklist.\
![REPLACE ME WITH YOUR ALT TEXT](https://cdn.modrinth.com/data/nILG1dtG/images/11c25510bcd38cd87445f3241d44707424e8dbb9.gif)
- **Item Exclusion and Public Trash Bin Handling:**\
Players can add items to the home trash bin blacklist to prevent their recycling.
Excluded items go directly to the public trash bin.
Configuration includes support for excluding items with special lore (e.g., items from the QS shop with 
showLocation).
Public trash bins can either clear new items during each sweep or accumulate all trash without clearing.\
![REPLACE ME WITH YOUR ALT TEXT](https://cdn.modrinth.com/data/nILG1dtG/images/05e44d7c3286d7daa0d0222338b780168b423f94.gif)
- **Entity Cleanup:**\
Optional cleanup of all animals/monsters.
Command (/WorldListTrashCan Look) to view entity types and configure entity blacklists/whitelists.
Support for cleaning special entities from mod.\
![REPLACE ME WITH YOUR ALT TEXT](https://cdn.modrinth.com/data/nILG1dtG/images/897ed52ff78147da390920cb895e68a65811ed18.png)
- **Dense Entity Cleanup:**\
Configurable radius, target quantity, kill or remove options, and player notifications for dense entity 
cleanup.\
![REPLACE ME WITH YOUR ALT TEXT](https://cdn.modrinth.com/data/nILG1dtG/images/3c34031e1638855ab24b51c3aff80e05e68ca62b.gif)
- **Spam/Command Limitations:**\
Configurable chat/command intervals, whitelist commands, and informative messages.
Defense against item dropping.
Players can manually enable or disable the item-drop defense mode.

- **Performance Optimization:**\
  All optimizations have configurable options in the settings.
  1. Quick removal of uncollectible arrows (e.g., arrows from skeletons or bows with infinite 
enchantments).
  2. Prohibit players from trampling on farmland

### How to Install
---
- **Throw it into the Plugins folder**

### Commands:
---
- **/wtc reload**\
  Reloads the plugin.
- **/wtc GlobalTrash**\
  Opens the public trash bin.
- **/wtc help**\
  Displays help information.
- **/wtc ban**\
  Opens the local world trash blacklist GUI.
- **/wtc Look**\
  Right-click to get entity type (useful for adding to the configuration blacklist/whitelist).
- **/wtc GlobalBan**\
  Opens the global world trash blacklist GUI.
- **/wtc add [WorldName] [Quantity]**\
  Sets the maximum quantity for the trash bin in the specified world (defaults to the world at your feet if not specified).
- **/wtc DropMode**\
  Opens or closes drop mode.

### Permission
---
- **WorldListTrashCan.BanGui**:\
Permission to open the GUI for adding items to the player's home trash bin blacklist.
- **WorldListTrashCan.GlobalTrashOpen**: \
Permission to open the global trash bin.
- **WorldListTrashCan.help**: \
Permission to access command help.
- **WorldListTrashCan.DropMode**: \
Permission to open or close drop mode.\
***(The above is available by default)***
- **WorldListTrashCan.GlobalBan**: \
Permission to open the global trash bin blacklist GUI.
- **WorldListTrashCan.Look**: \
Permission to right-click and query the entity type.\
***(The above is not available by default)***


**Spigot Introduction Page and Download Link (with Image)**  
[https://www.spigotmc.org/resources/optimization-management-cleanup-%E2%AD%90-worldlisttrashcan-%E2%AD%90-more-than-just-a-cleanup-plugin.113816/](https://www.spigotmc.org/resources/optimization-management-cleanup-%E2%AD%90-worldlisttrashcan-%E2%AD%90-more-than-just-a-cleanup-plugin.113816/)

**Modrinth Introduction Page and Download Link (with Image)**  
[https://modrinth.com/plugin/worldlisttrashcan](https://modrinth.com/plugin/worldlisttrashcan)



