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
import net.minecraftforge.gradle.userdev.UserDevExtension
import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace
import org.anti_ad.mc.configureCommon
import org.anti_ad.mc.forgeCommonAfterEvaluate
import org.anti_ad.mc.platformsCommonConfig
import org.anti_ad.mc.registerMinimizeJarTask
import proguard.gradle.ProGuardTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val supported_minecraft_versions = listOf("1.17", "1.17.1")
val mod_loader = "forge"
val mod_version = project.version
val minecraft_version = "1.17.1"
val forge_version = "37.1.1"
val mod_artefact_version = project.ext["mod_artefact_version"]


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
        classpath("com.guardsquare:proguard-gradle:7.2.1")
    }
}



configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(30, "seconds")
}

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
    signing
    id("com.matthewprenger.cursegradle")
    id("com.modrinth.minotaur")
    id("com.github.johnrengelman.shadow")
}

configureCommon()
platformsCommonConfig()


java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.5"
    jvmTarget = "16"
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
}

val fg: net.minecraftforge.gradle.userdev.DependencyManagementExtension = project.extensions["fg"] as net.minecraftforge.gradle.userdev.DependencyManagementExtension

dependencies {
    "shadedApi"(project(":common"))

    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.21")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")

    "minecraft"("net.minecraftforge:forge:$minecraft_version-$forge_version")
    //"implementation"("org.spongepowered:mixin:0.8.3-SNAPSHOT")
    "annotationProcessor"("org.spongepowered:mixin:0.8.3-SNAPSHOT:processor")
    "testAnnotationProcessor"("org.spongepowered:mixin:0.8.3-SNAPSHOT:processor")

    runtimeOnly ( fg.deobf("curse.maven:nekoration-508028:3579060"))
}

afterEvaluate {
    project.sourceSets.getByName("main") {
        this.java.srcDirs("./src/shared/java")
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
    relocate("kotlin", "org.anti_ad.embedded.kotlin")
    relocate("kotlinx", "org.anti_ad.embedded.kotlinx")

    //include("org/anti_ad/mc/**")

    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_module")
    exclude("**/*.kotlin_builtins")
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
    exclude("META-INF/LICENSE")
    exclude("META-INF/README")

    minimize()
}


tasks.register<Copy>("copyProGuardJar") {

    var shadow = tasks.getByName<ShadowJar>("shadowJar");
    val fromJarName = shadow.archiveBaseName.get()
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
        libraryjars(configurations.runtimeClasspath.get().files.filter {
            !it.name.contains("InventoryProfilesNext-common")
        })
    }

}

val customJar by dummyJar( // dummy jar
    thisJarNam = "",
    fromJarNam = ""
                         )

fun dummyJar(thisJarNam: String, fromJarNam: String) = tasks.creating(Jar::class) { // dummy jar for reobf
    var shadow = tasks.getByName<ShadowJar>("shadowJar");
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
    mappings(mapOf(
        "channel" to "official",
        "version" to "1.17.1"
                  ))
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
                "bsl.debug" to "true"
                            ))
            arg("--mixin.config=mixins.ipnext.json")
            workingDirectory = project.file("run").canonicalPath
            source(org.anti_ad.mc.FilteringSourceSet(sourceSets["main"], "InventoryProfilesNext-common", logger))


            jvmArg("--add-exports=java.base/sun.security.util=ALL-UNNAMED")
            jvmArg("--add-opens=java.base/java.util.jar=ALL-UNNAMED")
            //taskName = "plamenRunClient"
            this.forceExit = false
        }
        val action = create("client", runConfig)

        rcltName = action.taskName

        create("server", runConfig)
        //create("data", runConfig)
        all {
            lazyToken("minecraft_classpath") {
                configurations["shadedApi"].copyRecursive().resolve().filter {
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


tasks.register<Copy>("injectCommonResources") {
    tasks["prepareRuns"].dependsOn("injectCommonResources")
    dependsOn(":common:processResources")
    from(project(":common").layout.buildDirectory.dir("resources/main"))
    include("assets/**")
    into(project.layout.buildDirectory.dir("resources/main"))
}




tasks.register<DefaultTask>("fixRunJvmArgs") {
    tasks["prepareRuns"].finalizedBy("fixRunJvmArgs")


    mustRunAfter("prepareRunClient")

    doLast {
        val ts = tasks.named(rcltName, JavaExec::class)

        val newArgs = mutableListOf<String>()

        val commonPath = project(":common").buildDir.absolutePath + "/classes/"
        val javaCommon = commonPath + "java/main"
        val kotlinCommon = commonPath + "kotlin/main"
        ts.get().environment["MOD_CLASSES"] = "${ts.get().environment["MOD_CLASSES"]}:$javaCommon"
        ts.get().environment["MOD_CLASSES"] = "${ts.get().environment["MOD_CLASSES"]}:$kotlinCommon"
        val newClassPath = project.files()
        newClassPath.from(File(javaCommon))
        newClassPath.from(File(kotlinCommon))
        newClassPath.from(ts.get().classpath)
        ts.get().classpath = newClassPath
        //ts.get().classpath(File(commonPath + "java/main"))
        //ts.get().classpath(File(commonPath + "kotlin/main"))

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
                var newValue: String = ""
                split.forEach { cp ->
                    if (!cp.contains("InventoryProfilesNext-common")) {
                        if (newValue != "") {
                            newValue = "$newValue:$cp"
                        } else {
                            newValue = cp
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

tasks.register<Delete>("removeCommonResources") {
    tasks["prepareRuns"].finalizedBy("removeCommonResources")
    doLast {
        delete(project.layout.buildDirectory.dir("resources/main/assets"))
    }

    mustRunAfter("runClient")
}

gradle.buildFinished {
}

afterEvaluate {
    tasks.forEach {
        logger.info("*******************8found task: {} {} {}", it, it.name, it.group)
    }

}

tasks.register<Jar>("deobfJar") {
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




configure<CurseExtension> {

    if (System.getenv("CURSEFORGE_DEPOY_TOKEN") != null && System.getenv("IPNEXT_RELEASE") != null) {
        apiKey = System.getenv("CURSEFORGE_DEPOY_TOKEN")
    }

    project(closureOf<CurseProject> {
        id = "495267"
        changelogType = "markdown"
        changelog = file("../../description/out/pandoc-release_notes.md")
        releaseType = "beta"
        supported_minecraft_versions.forEach {
            if (!it.toLowerCase().contains("pre") && !it.toLowerCase().contains("shanpshot")) {
                this.addGameVersion(it)
            }
        }
        val forgeReobfJar = tasks.named<Jar>("shadowJar").get()
        val remappedJarFile = forgeReobfJar.archiveFile.get().asFile
        mainArtifact(remappedJarFile, closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            displayName = "Inventory Profiles Next-$mod_loader-$minecraft_version-$mod_version"
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


modrinth {

    this.failSilently.set(true)

    if (System.getenv("IPNEXT_RELEASE") != null) {
        token.set(System.getenv("MODRINTH_TOKEN"))
    }

    projectId.set("O7RBXm3n")
    versionNumber.set("$mod_loader-$minecraft_version-$mod_version") // Will fail if Modrinth has this version already
    val forgeReobfJar = tasks.named<Jar>("shadowJar").get()
    val remappedJarFile = forgeReobfJar.archiveFile
    uploadFile.set(remappedJarFile as Any) // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
    gameVersions.addAll(supported_minecraft_versions)
    logger.lifecycle("""
        +*************************************************+
        Will release ${remappedJarFile.get().asFile.path}
        +*************************************************+
    """.trimIndent())
    versionName.set("IPN $mod_version for $mod_loader $minecraft_version")
    this.changelog.set(project.rootDir.resolve("description/out/pandoc-release_notes.md").readText())
    loaders.add(mod_loader)
}




tasks.register<Copy>("copyJavadoc") {
    dependsOn(":common:packageJavadoc")

    val javadocJar = project(":common").tasks.named<Jar>("packageJavadoc").get()
    from(javadocJar)
    into(layout.buildDirectory.dir("publish"))
    rename {
        "$mod_loader-$minecraft_version-$mod_artefact_version-javadoc.jar"
    }
    logger.lifecycle("will rename ${javadocJar.archiveFile.get().asFile} to $mod_loader-$minecraft_version-$mod_artefact_version.jar" )
}

val prepareSourceJar = tasks.register<Copy>("prepareSourceJar") {
    dependsOn(":common:generateGrammarSource")
    dependsOn(":common:generateTestGrammarSource")
    val commonKotlinSources = project(":common").layout.projectDirectory.dir("src/main/java")
    val commonAntlrSources = project(":common").layout.projectDirectory.dir("src/main/java")
    val commonGeneratedSources = project(":common").layout.buildDirectory.dir("generated-src/antlr/main")
    val platformSources = layout.projectDirectory.dir("src/main/java")
    from(commonKotlinSources) {
        include("**/*.java")
        include("**/*.kt")
    }
    from(commonGeneratedSources) {
        include("**/*.java")
        include("**/*.tokens")
        include("**/*.interp")
    }
    from(commonAntlrSources) {
        include("**/*.g4")
    }
    from(platformSources) {
        include("**/*.java")
        include("**/*.kt")
    }
    into(layout.buildDirectory.dir("srcJarContent"))
}

tasks.register<org.gradle.jvm.tasks.Jar>("packageSources") {
    dependsOn("prepareSourceJar")
    archiveClassifier.set("sources")
    archiveBaseName.set("$mod_loader-$minecraft_version-$mod_artefact_version")
    archiveVersion.set("")
    destinationDirectory.set(layout.buildDirectory.dir("publish"))

    from(layout.buildDirectory.dir("srcJarContent"))

}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.anti-ad.mc"
            artifactId = "inventory-profiles-next"
            version = "$mod_loader-$minecraft_version-$mod_artefact_version"
            val mainArtefact = layout.buildDirectory.file("publish/$mod_loader-$minecraft_version-$mod_artefact_version.jar")
            val javadocArtefact = layout.buildDirectory.file("publish/$mod_loader-$minecraft_version-$mod_artefact_version-javadoc.jar")
            val sourcesArtefact = layout.buildDirectory.file("publish/$mod_loader-$minecraft_version-$mod_artefact_version-sources.jar")
            artifact(mainArtefact)
            artifact(javadocArtefact) {
                classifier = "javadoc"
            }
            artifact(sourcesArtefact) {
                classifier = "sources"
            }
            pom {
                url.set("https://inventory-profiles-next.github.io/")
                this.name.set("Inventory Profiles Next")
                description.set("""
                    Client side Minecraft MOD that adds multiple features to help you keep your inventory organized. 
                """.trimIndent())
                scm {
                    val connectionURL = "scm:git:https://github.com/blackd/Inventory-Profiles"
                    connection.set(connectionURL)
                    developerConnection.set(connectionURL)
                    url.set("https://github.com/blackd/Inventory-Profiles")
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/blackd/Inventory-Profiles/all-in-one/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("mirinimi")
                        name.set("Plamen K. Kosseff")
                        email.set("plamen@anti-ad.org")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "local"
            val rloc = rootProject.layout.projectDirectory.dir("repo/releases")
            val sloc = rootProject.layout.projectDirectory.dir("repo/snapshots")

            setUrl {
                if (version.toString().endsWith("SNAPSHOT"))
                    sloc
                else
                    rloc
            }
        }
    }
}

val hasSigningKey = project.hasProperty("signingKeyId") || project.hasProperty("signingKey")
if(hasSigningKey) {
    doSign(project)
}

fun doSign(project: Project) {
    project.signing {
        setRequired { project.gradle.taskGraph.hasTask("publish") }

        val signingKeyId: String? = project.findProperty("signingKeyId") as String?
        val signingKey: String? = project.findProperty("signingKey") as String?
        val signingPassword: String? = project.findProperty("signingPassword") as String?
        if (signingKeyId != null) {
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        } else if (signingKey != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign(publishing.publications.getByName("maven"))
    }
}
