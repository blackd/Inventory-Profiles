
<div style="align-content: center; display: block; margin: 10px auto; width: 100%;">
<div align="center" style="vertical-align: center; horiz-align: center;">

[![1][1]][2]  [![5][5]][6] [![3][3]][4]

</div>
<div align="center" style="vertical-align: center; horiz-align: center;">

[![11][11]][12] **[![8][8]][7]** [![Javadoc][10]][9]  

</div>
</div>

# Inventory Profiles Next
**Requirement:** Minecraft 1.16.5 - 1.21, Fabric Loader 0.11.6, Forge 36.1.32
## Mod download

**Modrinth**: https://modrinth.com/mod/inventory-profiles-next

**CurseForge**: https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next

# Other MODs integration

## gradle:
Gradle examples are in kotlin DSL. 
Although currently there is no difference between fabric and forge APIs this is not a guarantee, we advise you to add dependency on the proper loader version.

### Repository

```kotlin
repositories {
    maven {
        name = "IPN-Releases"
        mavenContent {
            releasesOnly()
        }
        content {
            includeGroup("org.anti_ad.mc")
            includeGroup("ca.solo-studios")
        }
        url = uri("https://maven.ipn-mod.org/releases")
    }
}
```

### Fabric
```kotlin
dependencies {
    compileOnly(group = "org.anti-ad.mc",
                name = "InventoryProfilesNext-fabric-1.21",
                version = "2.0.1")
    // for libIPN usually you don't need it
    compileOnly(group = "org.anti-ad.mc",
                name = "libIPN-fabric-1.21",
                version = "5.0.1",
                classifier = "dev")
}
```

### Forge
```kotlin
dependencies {
    compileOnly(group = "org.anti-ad.mc",
                name = "InventoryProfilesNext-forge-1.21",
                version = "2.0.1")
    // for libIPN usually you don't need it
    compileOnly(group = "org.anti-ad.mc",
                name = "libIPN-forge-1.21",
                version = "5.0.1",
                classifier = "dev")
}
```

### Maven

Who uses maven still :D


### TODO
 - learn Kotlin
 - learn about Minecraft modding :)


[1]: https://img.shields.io/github/downloads/blackd/Inventory-Profiles/total?style=plastic&label=GitHub%0aDownloads&logo=github
[2]: https://github.com/blackd/Inventory-Profiles/releases/latest
[3]: https://cf.way2muchnoise.eu/full_495267_CurseForge%20Downloads_%20.svg
[4]: https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next
[5]: https://img.shields.io/badge/dynamic/json?color=00AF5C&label=Modrinth%0aDownloads%20&style=plastic&logo=modrinth&query=downloads&url=https://api.modrinth.com/v2/project/O7RBXm3n
[6]: https://modrinth.com/mod/inventory-profiles-next
[7]: https://discord.gg/23YCxmveUM
[8]: https://img.shields.io/discord/861171785897738240?label=Discord&logo=discord&style=plastic
[9]: https://javadoc.io/doc/org.anti-ad.mc/inventory-profiles-next/fabric-1.17.1-1.1.0
[10]: https://javadoc.io/badge2/org.anti-ad.mc/inventory-profiles-next/fabric-1.17.1-1.1.0/javadoc.svg
[11]: https://img.shields.io/badge/Available%20for-MC%201.14%20to%201.21-c70039
[12]: https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next/files
