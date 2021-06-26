import org.anti_ad.mc.configureCommon
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.modrinth.minotaur.TaskModrinthUpload
import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.task.RemapJarTask
import proguard.gradle.ProGuardTask

plugins {
    `java-library`
    id("fabric-loom").version("0.8.9")
   // kotlin("jvm") version "1.4.32"
    `maven-publish`
    antlr

    id("com.modrinth.minotaur").version("1.1.0")
}

configureCommon()

group = "org.anti_ad.mc.fabric_1_17"


dependencies {
    "shadedApi"(project(":common"))
    "implementation"("org.apache.commons:commons-rng-core:1.3")
    "implementation"("commons-io:commons-io:2.4")
    val antlrVersion = "4.9.1"
    "antlr"("org.antlr:antlr4:$antlrVersion")
    "implementation"("org.antlr:antlr4-runtime:$antlrVersion")

    implementation("com.guardsquare:proguard-gradle:7.1.0-beta5")
    minecraft("com.mojang:minecraft:1.17")
    mappings("net.fabricmc:yarn:1.17+build.5:v2")
    modImplementation("net.fabricmc:fabric-loader:0.11.6")


    modImplementation("com.terraformersmc:modmenu:2.0.2")
}

minecraft{

}

tasks.named<AntlrTask>("generateGrammarSource") {
    enabled = false
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
    addDefaultNestedDependencies.set(false)
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
        libraryjars( configurations.runtimeClasspath.get().files)
    }
    dependsOn(tasks["shadowJar"])
}

