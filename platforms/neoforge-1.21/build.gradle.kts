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
import net.neoforged.gradle.dsl.common.runs.run.Run
import org.anti_ad.mc.ipnext.buildsrc.FilteringSourceSet
import org.anti_ad.mc.ipnext.buildsrc.configureCommon
import org.anti_ad.mc.ipnext.buildsrc.fgdeobf
import org.anti_ad.mc.ipnext.buildsrc.forgeCommonAfterEvaluate
import org.anti_ad.mc.ipnext.buildsrc.neoForgeCommonDependency
import org.anti_ad.mc.ipnext.buildsrc.platformsCommonConfig
import org.anti_ad.mc.ipnext.buildsrc.registerMinimizeJarTask
import proguard.gradle.ProGuardTask
import kotlin.math.log

val supported_minecraft_versions = listOf("1.21")
val mod_loader = "neoforge"
val mod_version = project.version
val minecraft_version = "1.21"
val minecraft_version_string = "1.21"
val forge_version = "21.0.61-beta"
val mod_artefact_version = project.ext["mod_artefact_version"]
val kotlin_for_forge_version = "5.3.0"
val mappingsMap = mapOf("channel" to "official",
                        "version" to "1.21")
val libIPN_version = "${project.name}:${project.ext["libIPN_version"]}"

logger.lifecycle("""
    ***************************************************
    Processing "${project.path}"
    supported versions: $supported_minecraft_versions
    loader: $mod_loader
    mod version: $mod_version
    building against MC: $minecraft_version
    ***************************************************
    """.trimIndent())

buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
    }
    dependencies {
        classpath(group = "org.spongepowered", name = "mixingradle", version = "0.7+" )
        classpath("com.guardsquare:proguard-gradle:7+")
    }
}


/*
configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(30, "seconds")
}

 */

//apply(from = "https://raw.githubusercontent.com/SizableShrimp/Forge-Class-Remapper/main/classremapper.gradle")

//I have no idea why but these MUST be here and not in plugins {}...

//apply(plugin = "org.spongepowered.mixin")



plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    java
    idea
    `maven-publish`
    antlr
    signing
    id("com.matthewprenger.cursegradle")
    id("com.modrinth.minotaur")
    id("io.github.goooler.shadow")
    id("net.neoforged.gradle.userdev")
    id ("net.neoforged.gradle.mixin") version "7.+"

}

configureCommon()
platformsCommonConfig()

java.toolchain.languageVersion = JavaLanguageVersion.of(21)
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
    maven {
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup ("curse.maven")
        }
    }
    gradlePluginPortal()
/*
    maven {
        name = "kotlinforforge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
*/
}


neoForgeCommonDependency(minecraft_version, forge_version, kotlin_for_forge_version, libIPN_version)

configurations {
    create("embed")
}

dependencies {
    //api(fg.deobf("org.anti_ad.mc:libIPN-$libIPN_version"))
    //api("org.anti_ad.mc:libIPN-$libIPN_version")

/*
    runtimeOnly( fg.deobf("curse.maven:athena-841890:4686264"))
    runtimeOnly(fg.deobf("curse.maven:resourcefullib-570073:4681831"))
*/
   compileOnly("curse.maven:chipped-456956:4634856")
   compileOnly("curse.maven:easy-villagers-400514:4584220")
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


tasks.named<AntlrTask>("generateGrammarSource").configure {
    val pkg = "org.anti_ad.mc.common.gen"
    outputDirectory = file("build/generated-src/antlr/main/${pkg.replace('.', '/')}")
    arguments = listOf(
        "-visitor", "-package", pkg,
        "-Xexact-output-dir"
                      )
}

afterEvaluate {
    project.sourceSets.getByName("main") {
        this.java.srcDirs("./src/shared/java")
        this.java.srcDirs("./src/shared/kotlin")
        project.layout.projectDirectory.dir("src/integrations").asFile.walk().maxDepth(1).forEachIndexed() { i, it ->
            if (i > 0 && it.isDirectory) {
                this.java.srcDirs(it.path + "/src/main/java")
                this.java.srcDirs(it.path + "/src/main/kotlin")
            }
        }
    }
    project.sourceSets.getByName("main") {
        resources.srcDirs("src/shared/resources")
//        resources.srcDirs("src/main/resources")
        resources.srcDirs.forEach {
            logger.lifecycle("found resource dir: ${it.absolutePath}")
        }
    }
/*
    sourceSets.forEach {
        val dir = layout.buildDirectory.dir("sourcesSets/${it.name}")
        it.output.setResourcesDir(dir.get().asFile)
        it.java.destinationDirectory = dir
        it.kotlin.destinationDirectory = dir
    }
*/
}

tasks.withType<JavaCompile>().all {
    dependsOn("processResources")
}


if ("true" == System.getProperty("idea.sync.active")) {
    afterEvaluate {
        tasks.withType<JavaCompile>().all {
            options.annotationProcessorPath = files()
        }
    }
}




tasks.register<Copy>("copyMixinMappings") {
    dependsOn("compileJava")
    tasks["classes"]?.dependsOn("copyMixinMappings")
    val inName = layout.buildDirectory.file("tmp/compileJava/mixin.refmap.json")
    val outName = layout.buildDirectory.file("resources/main/")
    from(inName)
    into(outName)
    rename {
        "ipnext.refmap.json"
    }
}


tasks.jar {
    manifest {
        attributes(mapOf(
            "MixinConfigs" to "mixins.ipnext.json"
        ))
    }
    dependsOn("copyMixinMappings")
}

val shadowJarTask: ShadowJar = tasks.named<ShadowJar>("shadowJar") {

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
    exclude("META-INF/com.android.tools/**")
    exclude("META-INF/proguard/**")
    exclude("META-INF/services/**")
    //exclude("META-INF/LICENSE")
    //exclude("META-INF/README")
    dependsOn("copyMixinMappings")
    minimize()
}.get()




val proguard by tasks.registering(ProGuardTask::class) {

    configuration("../../proguard.txt")
    printmapping {
        project.layout.buildDirectory.file("proguard/mappings.map")
    }
    // project(":platforms:fabric_1_17").tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get().archiveFileName

    val outName = shadowJarTask.archiveFileName.get().replace("-shaded", "-all-proguard")
    dependsOn(shadowJarTask)
    //dependsOn("jar")
    logger.lifecycle(""" 
        ****************************
        Input name for proguard:
        build/libs/${shadowJarTask.archiveFileName}
        ****************************
    """.trimIndent())
    injars(shadowJarTask)
    outjars("build/libs/${outName}")

    doFirst {
        val classpath = configurations.runtimeClasspath.get().files + configurations.compileClasspath.get().files
        libraryjars( classpath)
    }

}

/*
val customJar by dummyJar()

fun dummyJar() = tasks.creating(Jar::class) { // dummy jar for reobf
    val shadow = tasks.getByName<ProGuardTask>("proguard")
    val fromJarName = shadow.outputs.files.first()
    val thisJarName = fromJarName.name.replace("-all-proguard", "")
    archiveFileName.set(thisJarName)
    dependsOn(tasks["proguard"])
    doLast {
        copy {
            from("build/libs/$fromJarName-all-proguard.jar")
            into("build/libs")
            rename { thisJarName }
        }
    }
    //finalizedBy(tasks["copyProGuardJar"])
}
*/



val minimizeJar = registerMinimizeJarTask()

afterEvaluate {
    forgeCommonAfterEvaluate(mod_loader, minecraft_version, mod_artefact_version?.toString().orEmpty())
}

var rcltName = ""

configurations {
    implementation.get().extendsFrom(this.findByName("shadedApi"))
}

mixin {
    config("mixins.ipnext.json")
}

minecraft {
    mappings.version(mappingsMap)
    this.accessTransformers.file("src/main/resources/META-INF/accesstransformer.cfg")
}
runs {
    val runConfig = Action<Run> {
        systemProperties(mapOf(
            //"forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
            "forge.logging.console.level" to "debug",
            "mixin.env.remapRefMap" to "true",
//            "mixin.env.refMapRemappingFile" to "${projectDir}/build/createSrgToMcp/output.srg",
            "mixin.debug.verbose" to "true",
            "mixin.debug.export" to "true",
            "mixin.debug.dumpTargetOnFailure" to "true",
            "bsl.debug" to "true"))
        programArgument("--fml.mixin=mixins.ipnext.json")
        programArguments("--width=1280", "--height=720", "--username=DEV")

        jvmArgument("--add-exports=java.base/sun.security.util=ALL-UNNAMED")
        jvmArgument("--add-opens=java.base/java.util.jar=ALL-UNNAMED")
    }
    /*val action = */named("client", runConfig)
    named("client") {
        workingDirectory.set(project.file("run"))
    }

    //rcltName = action.taskName

    //create("data", runConfig)
}



val sourceJar = tasks.create<Jar>("sourcesJar") {
    from(sourceSets["main"]?.allSource)
    archiveClassifier.set("sources")
    exclude("org/anti_ad/mc/common/gen/*.tokens")
    dependsOn("generateGrammarSource")
}

afterEvaluate {
    tasks.forEach {
        logger.info("*******************8found task: {} {} {}", it, it.name, it.group)
    }

}

/*
val deobfJar = tasks.register<Jar>("deobfJar") {
    from(sourceSets["main"].output)
    archiveClassifier.set("dev")
    group = "forge"
}

val deobfElements = configurations.register("deobfElements") {
    isVisible = false
    description = "De-obfuscated elements for libs"
    isCanBeResolved = false
    isCanBeConsumed = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_API))
        attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
        attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.EXTERNAL))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 16)
    }
    outgoing.artifact(tasks.named("deobfJar"))
}
*/

/*
val javaComponent = components["java"] as AdhocComponentWithVariants

javaComponent.addVariantsFromConfiguration(deobfElements.get()) {
    mapToMavenScope("runtime")
}
*/

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
        create<MavenPublication>("maven") {
            groupId = "org.anti_ad.mc"
            artifactId = "${rootProject.name}-${project.name}"
            version = mod_artefact_version.toString()
            artifact(minimizeJar.outputs.files.first())
            artifact(sourceJar) {
                classifier = "sources"
            }
            //artifact(deobfJar)
        }
    }
    afterEvaluate {
        val publishTask = tasks["publishMavenPublicationToIpnOfficialRepoRepository"]
        if (publishTask != null) {
            publishTask.dependsOn(minimizeJar) //.dependsOn(customJar).dependsOn(sourceJar).dependsOn(deobfJar)
        } else {
            logger.error("Can't find publishMavenPublicationToIpnOfficialRepoRepository")
        }
        tasks["publishMavenPublicationToMavenLocal"]
            //?.dependsOn(customJar)
            ?.dependsOn(sourceJar)
            //?.dependsOn(deobfJar)
            ?.dependsOn(minimizeJar) ?: logger.error("Can't find publishMavenPublicationToIpnOfficialRepoRepository")
    }
}

configure<CurseExtension> {

    if (System.getenv("CURSEFORGE_DEPOY_TOKEN") != null && System.getenv("IPNEXT_RELEASE") != null) {
        apiKey = System.getenv("CURSEFORGE_DEPOY_TOKEN")
    }

    val clasifier = if (System.getenv("IPN_CLASSIFIER") != null) {
        System.getenv("IPN_CLASSIFIER")
    } else {
        ""
    }

    project(closureOf<CurseProject> {
        id = "495267"
        changelogType = "markdown"
        changelog = file("../../description/out/pandoc-release_notes.md")
        releaseType = "release"
        supported_minecraft_versions.forEach {
            if (!it.lowercase().contains("pre") && !it.lowercase().contains("shanpshot")) {
                this.addGameVersion(it)
            }
        }

        val forgeReobfJar = minimizeJar
        val remappedJarFile = forgeReobfJar.outputs.files.first().absoluteFile
        mainArtifact(remappedJarFile, closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            displayName = "Inventory Profiles Next-$mod_loader-$minecraft_version_string-$mod_version$clasifier"
        })

        afterEvaluate {
            uploadTask.dependsOn("build").dependsOn(minimizeJar)
        }
        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("kotlin-for-forge")
            requiredDependency("libipn")
        })
        addGameVersion("NeoForge")
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

    val clasifier = if (System.getenv("IPN_CLASSIFIER") != null) {
        System.getenv("IPN_CLASSIFIER")
    } else {
        ""
    }

    projectId.set("O7RBXm3n")
    versionNumber.set("$mod_loader-$minecraft_version-$mod_version$clasifier") // Will fail if Modrinth has this version already
    val forgeReobfJar = minimizeJar
    val remappedJarFile = forgeReobfJar.outputs.files.first().absoluteFile
    uploadFile.set(remappedJarFile as Any) // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
    gameVersions.addAll(supported_minecraft_versions)
    logger.lifecycle("""
        +*************************************************+
        Will release ${remappedJarFile.absolutePath}
        +*************************************************+
    """.trimIndent())
    versionName.set("IPN $mod_version for $mod_loader$clasifier $minecraft_version_string")
    this.changelog.set(project.rootDir.resolve("description/out/pandoc-release_notes.md").readText())
    loaders.add(mod_loader)
    dependencies.set(
        mutableListOf(
            ModDependency("ordsPcFz", "required"),
            ModDependency("onSQdWhM", "required")))
}
