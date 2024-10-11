/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.matthewprenger.cursegradle.CurseExtension
import com.matthewprenger.cursegradle.CurseProject
import com.modrinth.minotaur.dependencies.ModDependency
import net.fabricmc.loom.task.RemapJarTask
import org.anti_ad.mc.ipnext.buildsrc.Loaders.*
import org.anti_ad.mc.ipnext.buildsrc.configureCommon
import org.anti_ad.mc.ipnext.buildsrc.fabricCommonAfterEvaluate
import org.anti_ad.mc.ipnext.buildsrc.fabricCommonDependency
import org.anti_ad.mc.ipnext.buildsrc.fabricRegisterCommonTasks
import org.anti_ad.mc.ipnext.buildsrc.platformsCommonConfig
import org.anti_ad.mc.ipnext.buildsrc.registerMinimizeJarTask
import org.anti_ad.mc.ipnext.buildsrc.loom_version
import proguard.gradle.ProGuardTask

val supported_minecraft_versions = mapOf(MODRINTH to listOf("1.21", "1.21.1"),
                                         CURSEFORGE to listOf("1.21", "1.21.1"))
val mod_loader = "fabric"
val mod_version = project.version.toString()
val minecraft_version = "1.21.1"
val minecraft_version_string = "1.21.1"
val mappings_version = "1.21.1+build.3"
val loader_version = "0.16.3"
val modmenu_version = "11.0.0-beta.1"
val fabric_api_version = "0.103.0+1.21.1"
val mod_artefact_version = project.ext["mod_artefact_version"]
val libIPN_version = "${project.name}:${project.ext["libIPN_version"]}"
val carpet_core_version = "1.21-pre3-1.4.146+v240605"
val controlify_version = "2.0.0-beta.14+1.21-fabric"
val yacl_version = "3.5.0+1.21-fabric"

buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.+")
    }
}

logger.lifecycle("""
    ***************************************************
    Processing "${project.path}"
    supported versions: $supported_minecraft_versions
    loader: $mod_loader
    mod version: $mod_version
    building against MC: $minecraft_version
    loom version: $loom_version
    fabric api version: $fabric_api_version
    ***************************************************
    """.trimIndent())


plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-library`
    `maven-publish`
    antlr
    signing
    idea
    id("fabric-loom")
    id("com.matthewprenger.cursegradle")
    id("com.modrinth.minotaur")
    id("io.github.goooler.shadow")
}

configureCommon()
platformsCommonConfig()

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {

/*
    maven {
        name = "Ladysnake Libs"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
*/
    maven {
        name = "JourneyMap (Public)"
        url = uri("https://jm.gserv.me/repository/maven-public/")
    }

    maven("https://maven.resourcefulbees.com/repository/maven-public/")
}

fabricCommonDependency(minecraft_version,
                       mappings_version,
                       loader_version,
                       fabric_api_version,
                       modmenu_version = modmenu_version,
                       libIPN_version = libIPN_version,
                       carpet_version = carpet_core_version,
                       controlify_version = controlify_version,
                       yacl_version = yacl_version)

dependencies {
    //modRuntimeOnly("dev.emi:trinkets:3.4.0")
    //modRuntimeOnly("curse.maven:scout-631922:3947029")
    //"modCompileOnly"("com.terraformersmc:modmenu:$modmenu_version")
    //modRuntimeOnly("curse.maven:minihud-244260:4160116")
    //modRuntimeOnly("curse.maven:malilib-303119:4147598")


/*
    modImplementation("curse.maven:journey-map-32274:4841229")
    modImplementation("info.journeymap:journeymap-api-common:2.0.0-1.20.2-SNAPSHOT")
    modImplementation("info.journeymap:journeymap-api:2.0+1.20-fabric-SNAPSHOT")
*/

/*
    modImplementation("curse.maven:configured-457570:5441234")

    modImplementation("curse.maven:just-enough-professions-jep-417645:5539089")
    modImplementation("curse.maven:jei-238222:5598509")
*/
    modImplementation("curse.maven:packed-up-backpacks-361867:5547057")
    modImplementation("curse.maven:supermartijn642s-config-lib-438332:5546988")
    modImplementation("curse.maven:supermartijn642s-core-lib-454372:5546972")
    modImplementation("com.teamresourceful.resourcefullib:resourcefullib-fabric-1.21:3.0.9")

}

tasks.named("compileKotlin") {
    dependsOn("generateGrammarSource")
}

tasks.named("compileJava") {
    dependsOn("generateGrammarSource")
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn("generateGrammarSource")
}

plugins.withId("idea") {
    configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
        afterEvaluate {
            module.sourceDirs.add(file("src/shared/antlr"))
            module.sourceDirs.add(file("build/generated-src/antlr/main"))
            //module.generatedSourceDirs.add(file("build/generated-src/antlr/main"))
        }
    }
}

loom {
    runConfigs["client"].ideConfigGenerated(true)
    runConfigs["server"].ideConfigGenerated(true)
    runConfigs["client"].programArgs.addAll(listOf<String>("--width=1280", "--height=720", "--username=DEV"))
    runConfigs["server"].runDir = "runServer"
    mixin.defaultRefmapName.set("inventoryprofilesnext-refmap.json")

    accessWidenerPath.set(file("src/main/resources/ipnext.accesswidener"))
}

tasks.named<AntlrTask>("generateGrammarSource").configure {
    val pkg = "org.anti_ad.mc.common.gen"
    outputDirectory = file("build/generated-src/antlr/main/${pkg.replace('.', '/')}")
    arguments = listOf("-visitor", "-package", pkg,
                       "-Xexact-output-dir")
}


    project.sourceSets.getByName("main") {
        this.java.srcDirs("./src/shared/java")
        this.java.srcDirs("./src/shared/kotlin")
        project.layout.projectDirectory.dir("src/integrations").asFile.walk().maxDepth(1).forEachIndexed() { i, it ->
            if (i > 0 && it.isDirectory) {
                this.java.srcDirs(it.path + "/src/main/java")
                this.java.srcDirs(it.path + "/src/main/kotlin")
                logger.lifecycle("adding ${it.path + "/src/main/resources"} to resources dirs")
                this.resources.srcDirs(it.path + "/src/main/resources")

            }
        }
    }
    project.sourceSets.getByName("main") {
        resources.srcDirs("src/shared/resources")
    }


tasks.named<ShadowJar>("shadowJar") {

    configurations = listOf(project.configurations["shaded"])

    archiveClassifier.set("shaded")
    setVersion(project.version)

    relocate("org.antlr", "org.anti_ad.embedded.org.antlr")
    relocate("com.yevdo", "org.anti_ad.embedded.com.yevdo")

    exclude("kotlin/**")
    exclude("kotlinx/**")

    //exclude("META-INF/**")
    //exclude("**/*.kotlin_metadata")
    //exclude("**/*.kotlin_module")
    //exclude("**/*.kotlin_builtins")
    //exclude("**/*_ws.class") // fixme find a better solution for removing *.ws.kts
    //exclude("**/*_ws$*.class")
    exclude("**/*.stg")
    exclude("**/*.st")
    exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
    exclude("com/ibm/**")
    exclude("org/glassfish/**")
    exclude("org/intellij/**")
    exclude("org/jetbrains/**")
    exclude("org/jline/**")
    exclude("net/minecraftforge/**")
    exclude("io/netty/**")
    //exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
    exclude("META-INF/maven/**")
    //exclude("META-INF/LICENSE")
    //exclude("META-INF/README")

    minimize()
}

val proguard by tasks.registering(ProGuardTask::class) {

    configuration("../../proguard.txt")
    printmapping {
        project.layout.buildDirectory.file("proguard/mappings.map")
    }
    // project(":platforms:fabric_1_17").tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get().archiveFileName
    val fabricRemapJar = tasks.named<ShadowJar>("shadowJar").get()
    val inName = fabricRemapJar.archiveFile.get().asFile.absolutePath

    injars(inName)
    outjars("build/libs/${fabricRemapJar.archiveBaseName.get()}-all-proguard.jar")

    doFirst {
        val classpath = configurations.runtimeClasspath.get().files + configurations.compileClasspath.get().files
        libraryjars( classpath)
    }

    dependsOn(tasks["shadowJar"])
}

val remapped = tasks.named<RemapJarTask>("remapJar") {
    group = "fabric"
    val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
    val proGuardTask = tasks.getByName<ProGuardTask>("proguard")
    dependsOn(proGuardTask)
    //dependsOn("prepareRemapShadedJar")
    this.inputFile.set(File("build/libs/${shadowJar.archiveBaseName.get()}-all-proguard.jar"))
    //input.set( File("build/libs/${shadowJar.archiveBaseName.get()}-all-proguard.jar"))
    archiveFileName.set(shadowJar.archiveFileName.get().replace(Regex("-shaded\\.jar$"), ".jar"))
    addNestedDependencies.set(true)
    //addDefaultNestedDependencies.set(false)
    //remapAccessWidener.set(true)
}

val remapped2 = tasks.create<RemapJarTask>("remapJar2") {
    group = "fabric"
    val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    //dependsOn("prepareRemapShadedJar")
    this.inputFile.set(File("build/libs/${shadowJar.archiveFileName.get()}"))
    //input.set( File("build/libs/${shadowJar.archiveBaseName.get()}-all-proguard.jar"))
    archiveFileName.set(shadowJar.archiveFileName.get().replace(Regex("-shaded\\.jar$"), "-dev.jar"))
    addNestedDependencies.set(true)
    //addDefaultNestedDependencies.set(false)
    //remapAccessWidener.set(true)
}

fabricRegisterCommonTasks(mod_loader, minecraft_version, mod_artefact_version?.toString().orEmpty())

val minimizeJar = registerMinimizeJarTask()

val sourceJar = tasks.create<Jar>("sourcesJar") {
    from(sourceSets["main"]?.allSource)
    exclude("org/anti_ad/mc/common/gen/*.tokens")
    dependsOn("generateGrammarSource")
}

afterEvaluate {
    fabricCommonAfterEvaluate(mod_loader, minecraft_version, mod_artefact_version?.toString().orEmpty())
}

tasks.named<DefaultTask>("build") {
    dependsOn(remapped)
}


publishing {
    repositories {
        maven {
            /*
            val releasesRepoUrl = rootProject.layout.projectDirectory.dir("repos/releases")
            val snapshotsRepoUrl = rootProject.layout.projectDirectory.dir("repos/snapshots")
             */
            val releasesRepoUrl = "https://maven.ipn-mod.org/releases"
            val snapshotsRepoUrl = "https://maven.ipn-mod.org/snapshots"
            logger.lifecycle("project.ext[\"mod_artefact_is_release\"] = ${project.ext["mod_artefact_is_release"]}")
            name = "ipnOfficialRepo"
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
            url = uri(if (project.ext["mod_artefact_is_release"] as Boolean) releasesRepoUrl else snapshotsRepoUrl)
        }
    }
    publications {
        val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
        create<MavenPublication>("maven") {
            groupId = "org.anti_ad.mc"
            artifactId = "${rootProject.name}-${project.name}"
            version = mod_artefact_version.toString()

            artifact(shadowJar) {
                classifier = "shaded"
            }
            artifact(remapped) {
                classifier = "remapped"
            }
            artifact(sourceJar) {
                classifier = "sources"
            }
            artifact(minimizeJar.outputs.files.first()) {
                classifier = "prod"
            }
            artifact(remapped2) {
                classifier = "dev"
            }
            loom {
                this.disableDeprecatedPomGeneration(this@create)
            }
        }
        tasks["publishMavenPublicationToIpnOfficialRepoRepository"]
            ?.dependsOn(shadowJar)
            ?.dependsOn(remapped)
            ?.dependsOn(sourceJar)
            ?.dependsOn(remapped2)
            ?.dependsOn(minimizeJar)
        tasks["publishMavenPublicationToMavenLocal"]
            ?.dependsOn(shadowJar)
            ?.dependsOn(remapped)
            ?.dependsOn(sourceJar)
            ?.dependsOn(remapped2)
            ?.dependsOn(minimizeJar)
    }
}

// ============
// curseforge
// ============

configure<CurseExtension> {

    if (System.getenv("CURSEFORGE_DEPOY_TOKEN") != null && System.getenv("IPNEXT_RELEASE") != null) {
        apiKey = System.getenv("CURSEFORGE_DEPOY_TOKEN")
    }

    project(closureOf<CurseProject> {
        id = "495267"
        changelogType = "markdown"
        changelog = file("../../description/out/pandoc-release_notes.md")
        releaseType = "release"
        supported_minecraft_versions[CURSEFORGE]!!.forEach {
            this.addGameVersion(it)
        }
        this.addGameVersion("Fabric")
        this.addGameVersion("Quilt")
        val remappedJarFile = minimizeJar.outputs.files.first()
        logger.lifecycle("""
        +*************************************************+
        Will release ${remappedJarFile.path}
        +*************************************************+
    """.trimIndent())
        mainArtifact(remappedJarFile, closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            displayName = "Inventory Profiles Next-fabric-$minecraft_version_string-$mod_version"
        })

        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("fabric-api")
            requiredDependency("fabric-language-kotlin")
            requiredDependency("libipn")
            optionalDependency("modmenu")
        })
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

modrinth {

    this.failSilently.set(true)

    if (System.getenv("IPNEXT_RELEASE") != null) {
        token.set(System.getenv("MODRINTH_TOKEN"))
    }

    projectId.set("O7RBXm3n")
    versionNumber.set("$mod_loader-$minecraft_version-$mod_version") // Will fail if Modrinth has this version already
    val remappedJarFile = minimizeJar.outputs.files.first()
    uploadFile.set(remappedJarFile as Any) // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
    gameVersions.addAll(supported_minecraft_versions[MODRINTH]!!)
    logger.lifecycle("""
    +*************************************************+
    Will release ${remappedJarFile.path}
    +*************************************************+
""".trimIndent())
    versionName.set("IPN $mod_version for $mod_loader $minecraft_version_string")
    this.changelog.set(project.rootDir.resolve("description/out/pandoc-release_notes.md").readText())
    loaders.add(mod_loader)
    loaders.add("quilt")
    dependencies.set(
        mutableListOf(
            ModDependency("P7dR8mSH", "required"),
            ModDependency("Ha28R6CL", "required"),
            ModDependency("onSQdWhM", "required"),
            ModDependency("mOgUt4GM", "optional")))

    this.versionType.set(masecla.modrinth4j.model.version.ProjectVersion.VersionType.RELEASE.name)
}
