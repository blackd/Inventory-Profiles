import org.anti_ad.mc.configureCommon
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.task.RemapJarTask
import proguard.gradle.ProGuardTask
import com.modrinth.minotaur.TaskModrinthUpload;

val supported_minecraft_versions = listOf("1.16.5")
val mod_loader = "fabric"
val mod_version = project.version
val minecraft_version = "1.16.5"


logger.lifecycle("""
    ***************************************************
    Processing "${project.path}"
    supported versions: $supported_minecraft_versions
    loader: $mod_loader
    mod version: $mod_version
    building against MC: $minecraft_version
    loom version:     $loom_version_116
    ***************************************************
    """.trimIndent())

plugins {
    `java-library`
    id("fabric-loom").version(loom_version_116)
    `maven-publish`
    antlr
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.modrinth.minotaur") version "1.2.1"
}

configureCommon()

group = "org.anti_ad.mc.fabric_1_16"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    "shadedApi"(project(":common"))
    "implementation"("org.apache.commons:commons-rng-core:1.3")
    "implementation"("commons-io:commons-io:2.4")
    "implementation"("com.guardsquare:proguard-gradle:7.1.0-beta5")
    "minecraft"("com.mojang:minecraft:1.16.5")
    "mappings"("net.fabricmc:yarn:1.16.5+build.9:v2")
    "modImplementation"("net.fabricmc:fabric-loader:0.11.6")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:0.36.0+1.16")
    "modImplementation"("com.terraformersmc:modmenu:1.16.9")
}

minecraft{
    runConfigs["client"].runDir = "run/1.16.5"
    runConfigs["client"].programArgs += listOf("--width=1280", "--height=720", "--username=DEV")
}

afterEvaluate {

    tasks.register<Copy>("injectCommonResources") {
        dependsOn(":common:processResources")
        from(project(":common").layout.buildDirectory.dir("resources/main"))
        include("assets/**")
        into(project.layout.buildDirectory.dir("resources/main"))
    }

    tasks.register<Delete>("removeCommonResources") {
        this.delete(project.layout.buildDirectory.dir("resources/main/assets"))
    }

    tasks.getByName("runClient") {
        dependsOn("injectCommonResources")
        finalizedBy("removeCommonResources")
    }
}

tasks.named<DefaultTask>("build") {
    dependsOn(tasks["remapShadedJar"])
}

configure<LoomGradleExtension> {
    refmapName = "inventoryprofilesnext-refmap.json"
}

val remapped = tasks.register<RemapJarTask>("remapShadedJar") {
    group = "fabric"
    val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
    val proGuardTask = tasks.getByName<ProGuardTask>("proguard")
    dependsOn(proGuardTask)
    input.set( File("build/libs/${shadowJar.archiveBaseName.get()}-all-proguard.jar"))
    archiveFileName.set(shadowJar.archiveFileName.get().replace(Regex("-shaded\\.jar$"), ".jar"))
    addNestedDependencies.set(true)
    //addDefaultNestedDependencies.set(false)
    //remapAccessWidener.set(true)
}

val proguard by tasks.registering(ProGuardTask::class) {

    configuration("../../proguard.txt")

    // project(":platforms:fabric_1_17").tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get().archiveFileName
    val fabricRemapJar = tasks.named<ShadowJar>("shadowJar").get()
    val inName = fabricRemapJar.archiveFile.get().asFile.absolutePath

    injars(inName)
    outjars("build/libs/${fabricRemapJar.archiveBaseName.get()}-all-proguard.jar")

    doFirst {
        libraryjars( configurations.runtimeClasspath.get().files.filter {
            !it.name.contains("InventoryProfilesNext-common")
        })
    }
    dependsOn(tasks["shadowJar"])
}



configure<com.matthewprenger.cursegradle.CurseExtension> {

    if (System.getenv("CURSEFORGE_DEPOY_TOKEN") != null && System.getenv("IPNEXT_RELEASE") != null) {
        apiKey = System.getenv("CURSEFORGE_DEPOY_TOKEN")
    }

    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "495267"
        changelogType = "markdown"
        changelog = file("../../changelog.md")
        releaseType = "release"
        supported_minecraft_versions.forEach {
            if (!it.toLowerCase().contains("pre") && !it.toLowerCase().contains("shanpshot")) {
                this.addGameVersion(it)
            }
        }
        val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapShadedJar").get()
        val remappedJarFile = fabricRemapJar.archiveFile.get().asFile
        logger.lifecycle("""
            +*************************************************+
            Will release ${remappedJarFile.path}
            +*************************************************+
        """.trimIndent())
        mainArtifact(remappedJarFile, closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            displayName = "Inventory Profiles Next-fabric-$minecraft_version-$mod_version"
        })

        afterEvaluate {
            uploadTask.dependsOn("build")
        }

    })
    options(closureOf<com.matthewprenger.cursegradle.Options> {
        debug = false
        javaIntegration = false
        forgeGradleIntegration = mod_loader == "forge"
    })
}

// ============
// modrith
// ============


val publishModrinth by tasks.registering(TaskModrinthUpload::class) {

    onlyIf {
        System.getenv("MODRINTH_TOKEN") != null && System.getenv("IPNEXT_RELEASE") != null
    }

    token = System.getenv("MODRINTH_TOKEN") // An environment property called MODRINTH_TOKEN that is your token, set via Gradle CLI, GitHub Actions, Idea Run Configuration, or other

    projectId = "O7RBXm3n"
    versionNumber = "$mod_loader-$minecraft_version-$mod_version" // Will fail if Modrinth has this version already
    // On fabric, use 'remapJar' instead of 'jar'
    this.changelog
    val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapShadedJar").get()
    val remappedJarFile = fabricRemapJar.archiveFile
    uploadFile = remappedJarFile // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
    supported_minecraft_versions.forEach { ver ->
        addGameVersion(ver) // Call this multiple times to add multiple game versions. There are tools that can help you generate the list of versions
    }
    logger.lifecycle("""
        +*************************************************+
        Will release ${remappedJarFile.get().asFile.path}
        +*************************************************+
    """.trimIndent())
    versionName = "IPN $mod_version for $mod_loader $minecraft_version"
    changelog = project.rootDir.resolve("changelog.md").readText()
    addLoader(mod_loader)
}