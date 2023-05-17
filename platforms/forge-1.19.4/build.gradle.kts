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
import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import net.minecraftforge.gradle.userdev.UserDevExtension
import org.anti_ad.mc.ipnext.buildsrc.FilteringSourceSet
import org.anti_ad.mc.ipnext.buildsrc.configureCommon
import org.anti_ad.mc.ipnext.buildsrc.fgdeobf
import org.anti_ad.mc.ipnext.buildsrc.forgeCommonAfterEvaluate
import org.anti_ad.mc.ipnext.buildsrc.forgeCommonDependency
import org.anti_ad.mc.ipnext.buildsrc.platformsCommonConfig
import org.anti_ad.mc.ipnext.buildsrc.registerMinimizeJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import proguard.gradle.ProGuardTask

val supported_minecraft_versions = listOf("1.19.4")
val mod_loader = "forge"
val mod_version = project.version
val minecraft_version = "1.19.4"
val minecraft_version_string = "1.19.4"
val forge_version = "45.0.49"
val mod_artefact_version = project.ext["mod_artefact_version"]
val kotlin_for_forge_version = "4.1.0"
val mappingsMap = mapOf("channel" to "official",
                        "version" to "1.19.4")
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
        maven { url = uri("https://maven.minecraftforge.net/maven") }
        mavenCentral()

        //this is where out custom version of org.spongepowered.mixingradle is
        //I hope I'll be able to remove it soon
        maven {
            setUrl("../../temp/mixingradle-repo")
        }

        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
    }
    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = "5.+")
        classpath(group = "org.spongepowered", name = "mixingradle", version = "0.8.1-SNAPSHOT" )
        classpath("com.guardsquare:proguard-gradle:7.2.2")
    }
}


/*
configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(30, "seconds")
}

 */

apply(from = "https://raw.githubusercontent.com/SizableShrimp/Forge-Class-Remapper/main/classremapper.gradle")

//I have no idea why but these MUST be here and not in plugins {}...
apply(plugin = "net.minecraftforge.gradle")
apply(plugin = "org.spongepowered.mixin")



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
    id("com.github.johnrengelman.shadow")
}

configureCommon()
platformsCommonConfig()


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "org.anti-ad.mc"

repositories {
    maven { url = uri("https://maven.minecraftforge.net/maven") }
    mavenCentral()
    maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }

    maven {
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup ("curse.maven")
        }
    }
    gradlePluginPortal()
    maven {
        name = "kotlinforforge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

val fg: DependencyManagementExtension = project.extensions["fg"] as DependencyManagementExtension

fgdeobf =  { id ->
    fg.deobf(id)
}

forgeCommonDependency(minecraft_version, forge_version, kotlin_for_forge_version, libIPN_version)

configurations {
    create("embed")
}

dependencies {
    //runtimeOnly( fg.deobf("curse.maven:iron-furnaces-237664:4009901"))

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
    }
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
    exclude("META-INF/com.android.tools/**")
    exclude("META-INF/proguard/**")
    exclude("META-INF/services/**")
    //exclude("META-INF/LICENSE")
    //exclude("META-INF/README")

    minimize()
}


tasks.register<Copy>("copyProGuardJar") {

    val fabricRemapJar = tasks.named<ShadowJar>("shadowJar").get()
    val inName = layout.buildDirectory.file("libs/" + fabricRemapJar.archiveFileName.get().replace("-shaded", "-all-proguard"))
    val outName = fabricRemapJar.archiveFileName.get().replace("-shaded", "")
    logger.lifecycle("""
        
        ******************************
        will copy from: $inName
        to $outName
        ******************************
        
    """.trimIndent())
    from(inName)
    rename {
        outName
    }
    into(layout.buildDirectory.dir("libs"))
}

val proguard by tasks.registering(ProGuardTask::class) {

    configuration("../../proguard.txt")
    printmapping {
        project.layout.buildDirectory.file("proguard/mappings.map")
    }
    // project(":platforms:fabric_1_17").tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get().archiveFileName

    val fabricRemapJar = tasks.named<ShadowJar>("shadowJar").get()
    val inName = fabricRemapJar.archiveFileName.get().replace("-shaded", "")
    val outName = fabricRemapJar.archiveFileName.get().replace("-shaded", "-all-proguard")
    dependsOn(fabricRemapJar)
    dependsOn("jar")
    logger.lifecycle(""" 
        ****************************
        Input name for proguard:
        build/libs/${inName}
        ****************************
    """.trimIndent())
    injars("build/libs/${inName}")
    outjars("build/libs/${outName}")

    doFirst {
        val classpath = configurations.runtimeClasspath.get().files + configurations.compileClasspath.get().files
        libraryjars( classpath)
    }

}

val customJar by dummyJar()

fun dummyJar() = tasks.creating(Jar::class) { // dummy jar for reobf
    val shadow = tasks.getByName<ShadowJar>("shadowJar")
    val fromJarName = shadow.archiveBaseName
    val thisJarName = shadow.archiveFileName.get()
    archiveFileName.set(shadow.archiveFileName)
    dependsOn(tasks["proguard"])
    doLast {
        copy {
            from("build/libs/$fromJarName--all-proguard.jar")
            into("build/libs")
            rename { thisJarName }
        }
    }
    finalizedBy(tasks["copyProGuardJar"])
}


tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set(tasks.getByName<Jar>("jar").archiveBaseName.orNull) // Pain. Agony, even.
    archiveClassifier.set("") // Suffering, if you will.
    dependsOn("copyMixinMappings")
    //finalizedBy(tasks["customJar"])
}

registerMinimizeJarTask()

afterEvaluate {
    forgeCommonAfterEvaluate(mod_loader, minecraft_version, mod_artefact_version?.toString().orEmpty())
}

var rcltName = ""

configurations {
    implementation.get().extendsFrom(this.findByName("shadedApi"))
}

configure<UserDevExtension> {
    mappings(mappingsMap)
    this.accessTransformers("src/main/resources/META-INF/accesstransformer.cfg")

    runs {
        val runConfig = Action<RunConfig> {
            properties(mapOf(
                //"forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                "forge.logging.console.level" to "debug",
                "mixin.env.remapRefMap" to "true",
                "mixin.env.refMapRemappingFile" to "${projectDir}/build/createSrgToMcp/output.srg",
                "mixin.debug.verbose" to "true",
                "mixin.debug.export" to "true",
                "mixin.debug.dumpTargetOnFailure" to "true",
                "bsl.debug" to "true"))
            arg("--mixin.config=mixins.ipnext.json")
            //2560x1600
            args("--width=1280", "--height=720", "--username=DEV")
            workingDirectory = project.file("run").canonicalPath
            source(FilteringSourceSet(sourceSets["main"], "InventoryProfilesNext-common", logger))


            jvmArg("--add-exports=java.base/sun.security.util=ALL-UNNAMED")
            jvmArg("--add-opens=java.base/java.util.jar=ALL-UNNAMED")
            //taskName = "plamenRunClient"
            this.forceExit = false
        }
        val action = create("client", runConfig)

        rcltName = action.taskName

        val runConfigServer = Action<RunConfig> {
            properties(mapOf(
                //"forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                "forge.logging.console.level" to "debug",
                "mixin.env.remapRefMap" to "true",
                "mixin.env.refMapRemappingFile" to "${projectDir}/build/createSrgToMcp/output.srg",
                "mixin.debug.verbose" to "true",
                "mixin.debug.export" to "true",
                "mixin.debug.dumpTargetOnFailure" to "true",
                "bsl.debug" to "true"))
            arg("--mixin.config=mixins.ipnext.json")
            workingDirectory = project.file("run-server").canonicalPath
            source(FilteringSourceSet(sourceSets["main"], "InventoryProfilesNext-common", logger))


            jvmArg("--add-exports=java.base/sun.security.util=ALL-UNNAMED")
            jvmArg("--add-opens=java.base/java.util.jar=ALL-UNNAMED")
            //taskName = "plamenRunClient"
            this.forceExit = false
        }

        create("server", runConfigServer)
        //create("data", runConfig)

        all {
            lazyToken("minecraft_classpath") {
                project.tasks.findByPath(":platforms:${project.name}:runClient")?.dependsOn("fixRunJvmArgs")
                configurations["runHelperApi"].copyRecursive().resolve().filter {
                    it.absolutePath.contains("kotlin")
                }.joinToString(File.pathSeparator) {
                    it.absolutePath
                }
            }
        }

    }
    afterEvaluate {

    }
}


tasks.register<DefaultTask>("fixRunJvmArgs") {

    group = "forgegradle runs"

    mustRunAfter("prepareRunClient")

    doLast {
        val ts = tasks.named(rcltName, JavaExec::class)

        val newArgs = mutableListOf<String>()
        logger.lifecycle("Detected JVM Arguments:")
        ts.get().allJvmArgs.forEach {
            logger.lifecycle("\t$it")
        }

        ts.get().allJvmArgs.forEach {
            var processed = false

            if (it.startsWith("-DlegacyClassPath.file")) {
                val cpFile: String? = it.split("=").elementAtOrNull(1)
                if (cpFile != null) {

                    val f = File(cpFile)

                    val fcpPath = "${f.parentFile.path}/runtimeClasspath.txt"
                    logger.lifecycle("Checking if $fcpPath exists")
                    val fullCpFile = File(fcpPath)
                    val kotlinJars = mutableListOf<String>()
                    if (fullCpFile.exists()) {
                        kotlinJars.addAll(fullCpFile.readLines().filter { line ->
                            line.contains("kotlin")
                        })
                    }
                    val clean = f.readLines().filter { line ->
                        !line.contains("InventoryProfilesNext-common")
                    }
                    f.printWriter().use { pw ->
                        logger.lifecycle("Building new legacy classpath file")
                        kotlinJars.forEach { jar ->
                            logger.lifecycle("\tadding kotlin jar: $jar")
                            pw.println(jar)
                        }
                        clean.forEach { s ->
                            logger.lifecycle("\tadding other jar: $s")
                            pw.println(s)
                        }
                    }
                }
            }

            if (it.contains("InventoryProfilesNext-common")) {
                val split = it.split(":")
                var newValue = ""
                split.forEach { cp ->
                    if (!cp.contains("InventoryProfilesNext-common")) {
                        newValue = if (newValue != "") {
                            "$newValue:$cp"
                        } else {
                            cp
                        }
                    }
                }
                newArgs.add(newValue)
                processed = true
            }

            if (!processed) {
                newArgs.add(it)
            }

        }
        ts.get().allJvmArgs = newArgs
    }
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

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.addVariantsFromConfiguration(deobfElements.get()) {
    mapToMavenScope("runtime")
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
        create<MavenPublication>("maven") {
            groupId = "org.anti_ad.mc"
            artifactId = "${rootProject.name}-${project.name}"
            version = mod_artefact_version.toString()
            artifact(customJar)
            artifact(sourceJar) {
                classifier = "sources"
            }
            artifact(deobfJar)
        }
        tasks["publishMavenPublicationToIpnOfficialRepoRepository"]
            ?.dependsOn(customJar)
            ?.dependsOn(sourceJar)
            ?.dependsOn(deobfJar)
        tasks["publishMavenPublicationToMavenLocal"]
            ?.dependsOn(customJar)
            ?.dependsOn(sourceJar)
            ?.dependsOn(deobfJar)
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
            if (!it.toLowerCase().contains("pre") && !it.toLowerCase().contains("shanpshot")) {
                this.addGameVersion(it)
            }
        }
        val forgeReobfJar = tasks.named<Jar>("shadowJar").get()
        val remappedJarFile = forgeReobfJar.archiveFile.get().asFile
        mainArtifact(remappedJarFile, closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            displayName = "Inventory Profiles Next-$mod_loader-$minecraft_version_string-$mod_version$clasifier"
        })

        afterEvaluate {
            uploadTask.dependsOn("build")
        }
        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("kotlin-for-forge")
            requiredDependency("libipn")
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

    val clasifier = if (System.getenv("IPN_CLASSIFIER") != null) {
        System.getenv("IPN_CLASSIFIER")
    } else {
        ""
    }

    projectId.set("O7RBXm3n")
    versionNumber.set("$mod_loader-$minecraft_version-$mod_version$clasifier") // Will fail if Modrinth has this version already
    val forgeReobfJar = tasks.named<Jar>("shadowJar").get()
    val remappedJarFile = forgeReobfJar.archiveFile
    uploadFile.set(remappedJarFile as Any) // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
    gameVersions.addAll(supported_minecraft_versions)
    logger.lifecycle("""
        +*************************************************+
        Will release ${remappedJarFile.get().asFile.path}
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
