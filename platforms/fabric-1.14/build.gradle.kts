import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.anti_ad.mc.configureCommon
import proguard.gradle.ProGuardTask
import com.modrinth.minotaur.dependencies.ModDependency
import org.anti_ad.mc.fabricCommonAfterEvaluate
import org.anti_ad.mc.fabricCommonDependency
import org.anti_ad.mc.fabricRegisterCommonTasks
import org.anti_ad.mc.registerMinimizeJarTask

val supported_minecraft_versions = listOf("1.14.1", "1.14.2", "1.14.3", "1.14.4")
val mod_loader = "fabric"
val mod_version = project.version
val minecraft_version = "1.14.4"
val mappings_version = "1.14.4+build.18"
val mod_artefact_version = project.ext["mod_artefact_version"]
val loader_version = "0.14.4"
val modmenu_version = "1.7.17+build.1"
val fabric_api_version = "0.28.5+1.14"


logger.lifecycle("""
    ***************************************************
    Processing "${project.path}"
    supported versions: $supported_minecraft_versions
    loader: $mod_loader
    mod version: $mod_version
    building against MC: $minecraft_version
    loom version:     $loom_version
    ***************************************************
    """.trimIndent())

buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.1")
    }
}

logger.lifecycle("""
    ***************************************************
    Processing "${project.path}"
    supported versions: $supported_minecraft_versions
    loader: $mod_loader
    mod version: $mod_version
    building against MC: $minecraft_version
    loom version:     $loom_version
    ***************************************************
    """.trimIndent())



plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-library`
    id("fabric-loom")
    `maven-publish`
    signing
    id("com.matthewprenger.cursegradle")
    id("com.modrinth.minotaur")
    id("com.github.johnrengelman.shadow")

}

configureCommon(true)
//platformsCommonConfig()


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.6"
    jvmTarget = "1.8"
}

//this is here so we always compile for 1.8
tasks.withType<JavaCompile> {
    this.targetCompatibility = "1.8"
    this.sourceCompatibility = "1.8"
}

group = "org.anti-ad.mc"

fabricCommonDependency(minecraft_version,
                       mappings_version,
                       loader_version,
                       fabric_api_version)
dependencies {
    modImplementation("io.github.prospector:modmenu:1.7.17+build.1")
}



loom {
    runConfigs["client"].programArgs += listOf("--width=1280", "--height=720", "--username=DEV")
    mixin.defaultRefmapName.set("inventoryprofilesnext-refmap.json")
}

afterEvaluate {
    project.sourceSets.getByName("main") {
        this.java.srcDirs("./src/shared/java")
    }
}

tasks.named<ShadowJar>("shadowJar") {

    configurations = listOf(project.configurations["shaded"])

    archiveClassifier.set("shaded")
    setVersion(project.version)

    relocate("org.antlr", "org.anti_ad.embedded.org.antlr")
    relocate("com.yevdo", "org.anti_ad.embedded.com.yevdo")
    relocate("kotlin", "org.anti_ad.embedded.kotlin")
    relocate("kotlinx", "org.anti_ad.embedded.kotlinx")

    //include("assets/**")
    //include("org/anti_ad/mc/**")

    exclude("META-INF/**")
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
    exclude("META-INF/LICENSE")
    exclude("META-INF/README")

    minimize()
}



val proguard by tasks.register<ProGuardTask>("proguard") {

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
        libraryjars( configurations.runtimeClasspath.get().files.filter {
            !it.name.contains("InventoryProfilesNext-common")
        })
    }
    dependsOn(tasks["shadowJar"])
}



val remapped = tasks.named<RemapJarTask>("remapJar") {
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

fabricRegisterCommonTasks(mod_loader, minecraft_version, mod_artefact_version?.toString().orEmpty())

afterEvaluate {
    fabricCommonAfterEvaluate(mod_loader, minecraft_version, mod_artefact_version?.toString().orEmpty())
}

registerMinimizeJarTask()

tasks.named<DefaultTask>("build") {
    dependsOn(tasks["remapJar"])
    dependsOn("copyJavadoc")
    dependsOn("packageSources")
    dependsOn("copyJarForPublish")
    dependsOn("minimizeJar")
}

configure<com.matthewprenger.cursegradle.CurseExtension> {

    if (System.getenv("CURSEFORGE_DEPOY_TOKEN") != null && System.getenv("IPNEXT_RELEASE") != null) {
        apiKey = System.getenv("CURSEFORGE_DEPOY_TOKEN")
    }

    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "495267"
        changelogType = "markdown"
        changelog = file("../../description/out/pandoc-release_notes.md")
        releaseType = "release"
        supported_minecraft_versions.forEach {
            if (!it.toLowerCase().contains("pre") && !it.toLowerCase().contains("shanpshot")) {
                this.addGameVersion(it)
            }
        }
        this.addGameVersion("Fabric")
        val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapJar").get()
        val remappedJarFile = fabricRemapJar.archiveFile.get().asFile
        logger.lifecycle("""
            +*************************************************+
            Will release ${remappedJarFile.path}
            +*************************************************+
        """.trimIndent())
        mainArtifact(remappedJarFile, closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            displayName = "Inventory Profiles Next-fabric-$minecraft_version-$mod_version"
        })

        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("fabric-api")
            optionalDependency("modmenu")
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
    val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapJar").get()
    val remappedJarFile = fabricRemapJar.archiveFile
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
    dependencies.set(
        mutableListOf(
            ModDependency("P7dR8mSH","required"),
            ModDependency("mOgUt4GM","optional")))

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
                this.name.set("Inventory Profiles Next")
                url.set("https://inventory-profiles-next.github.io/")
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
