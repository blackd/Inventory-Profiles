[![1][1]][2]  [![5][5]][6] [![1][3]][4] **[![8][8]][7]** [![Javadoc][10]][9]  
# Inventory Profiles Next
**Requirement:** Minecraft 1.16.5 - 1.17, Fabric Loader 0.11.6, Forge 36.1.32
## Mod download

**Modrinth**: https://modrinth.com/mod/inventory-profiles-next

**CurseForge**: https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next

# Other MODs integration

The artefacts are available on Maven Central.
The Javadoc is available here [![Javadoc][10]][9]

## gradle:
Gradle examples are in kotlin DSL. 
Although currently there is no difference between fabric and forge APIs this is not a guarantee, we advise you to add dependency on the proper loader version.
### Fabric
```kotlin
dependencies {
    compileOnly(group = "org.anti-ad.mc",
                name = "inventory-profiles-next",
                version = "fabric-1.17.1-1.1.0")
}
```

### Forge
```kotlin
dependencies {
    compileOnly(group = "org.anti-ad.mc",
                name = "inventory-profiles-next",
                version = "forge-1.17.1-1.1.0")
}
```

### Maven
### Forge
```xml
<dependency>
    <groupId>org.anti-ad.mc</groupId>
    <artifactId>inventory-profiles-next</artifactId>
    <version>fabric-1.17.1-1.1.0</version>
</dependency>
```

### Forge
```xml
<dependency>
    <groupId>org.anti-ad.mc</groupId>
    <artifactId>inventory-profiles-next</artifactId>
    <version>forge-1.17.1-1.1.0</version>
</dependency>
```


### TODO
 - learn Kotlin
 - learn about Minecraft modding :)


[1]: https://img.shields.io/github/downloads/blackd/Inventory-Profiles/total?style=plastic&label=GitHub%0aDownloads&logo=github
[2]: https://github.com/blackd/Inventory-Profiles/releases/latest
[3]: https://img.shields.io/badge/dynamic/json?label=CurseForge%0aDownloads&query=downloadCount&url=https%3A%2F%2Faddons-ecs.forgesvc.net%2Fapi%2Fv2%2Faddon%2F495267&style=plastic&logo=curseforge&logoColor=red
[4]: https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next
[5]: https://img.shields.io/badge/dynamic/json?color=5da545&label=Modrinth%0aDownloads%20&query=downloads&url=https://api.modrinth.com/api/v1/mod/O7RBXm3n&style=plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMSAxMSIgd2lkdGg9IjE0LjY2NyIgaGVpZ2h0PSIxNC42NjciICB4bWxuczp2PSJodHRwczovL3ZlY3RhLmlvL25hbm8iPjxkZWZzPjxjbGlwUGF0aCBpZD0iQSI+PHBhdGggZD0iTTAgMGgxMXYxMUgweiIvPjwvY2xpcFBhdGg+PC9kZWZzPjxnIGNsaXAtcGF0aD0idXJsKCNBKSI+PHBhdGggZD0iTTEuMzA5IDcuODU3YTQuNjQgNC42NCAwIDAgMS0uNDYxLTEuMDYzSDBDLjU5MSA5LjIwNiAyLjc5NiAxMSA1LjQyMiAxMWMxLjk4MSAwIDMuNzIyLTEuMDIgNC43MTEtMi41NTZoMGwtLjc1LS4zNDVjLS44NTQgMS4yNjEtMi4zMSAyLjA5Mi0zLjk2MSAyLjA5MmE0Ljc4IDQuNzggMCAwIDEtMy4wMDUtMS4wNTVsMS44MDktMS40NzQuOTg0Ljg0NyAxLjkwNS0xLjAwM0w4LjE3NCA1LjgybC0uMzg0LS43ODYtMS4xMTYuNjM1LS41MTYuNjk0LS42MjYuMjM2LS44NzMtLjM4N2gwbC0uMjEzLS45MS4zNTUtLjU2Ljc4Ny0uMzcuODQ1LS45NTktLjcwMi0uNTEtMS44NzQuNzEzLTEuMzYyIDEuNjUxLjY0NSAxLjA5OC0xLjgzMSAxLjQ5MnptOS42MTQtMS40NEE1LjQ0IDUuNDQgMCAwIDAgMTEgNS41QzExIDIuNDY0IDguNTAxIDAgNS40MjIgMCAyLjc5NiAwIC41OTEgMS43OTQgMCA0LjIwNmguODQ4QzEuNDE5IDIuMjQ1IDMuMjUyLjgwOSA1LjQyMi44MDljMi42MjYgMCA0Ljc1OCAyLjEwMiA0Ljc1OCA0LjY5MSAwIC4xOS0uMDEyLjM3Ni0uMDM0LjU2bC43NzcuMzU3aDB6IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiM1ZGE0MjYiLz48L2c+PC9zdmc+
[6]: https://modrinth.com/mod/inventory-profiles-next
[7]: https://discord.gg/23YCxmveUM
[8]: https://img.shields.io/discord/861171785897738240?label=Discord&logo=discord&style=plastic
[9]: https://javadoc.io/doc/org.anti-ad.mc/inventory-profiles-next/fabric-1.17.1-1.1.0
[10]: https://javadoc.io/badge2/org.anti-ad.mc/inventory-profiles-next/fabric-1.17.1-1.1.0/javadoc.svg
