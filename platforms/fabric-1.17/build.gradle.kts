import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.modrinth.minotaur.TaskModrinthUpload
import net.fabricmc.loom.task.RemapJarTask
import org.anti_ad.mc.configureCommon
import org.anti_ad.mc.platformsCommonConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import proguard.gradle.ProGuardTask

val supported_minecraft_versions = listOf("1.17", "1.17.1")
val mod_loader = "fabric"
val mod_version = project.version.toString()
val minecraft_version = "1.17.1"
val mappings_version = "1.17.1+build.63"
val loader_version = "0.12.4"
val modmenu_version = "2.0.2"
val mod_artefact_version = project.ext["mod_artefact_version"]


logger.lifecycle("""
    ***************************************************
    Processing "${project.path}"
    supported versions: $supported_minecraft_versions
    loader: $mod_loader
    mod version: $mod_version
    building against MC: $minecraft_version
    loom version: $loom_version_117
    ***************************************************
    """.trimIndent())
/*
configurations.all {
    resolutionStrategy {
        force ("net.fabricmc:sponge-mixin:0.10.1+mixin.0.8.4")
    }
}
*/

plugins {
    `java-library`
    `maven-publish`
    signing
    id("fabric-loom").version(loom_version_117)
    antlr
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.modrinth.minotaur") version "1.2.1"
}

configureCommon()
platformsCommonConfig()

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.5"
    jvmTarget = "16"
}

//this is here so we always compile for 1.8
tasks.withType<JavaCompile> {
    this.targetCompatibility = "16"
}

group = "org.anti-ad.mc"


dependencies {
    "shadedApi"(project(":common"))
    implementation("com.guardsquare:proguard-gradle:7.1.1")
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$mappings_version:v2")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("com.terraformersmc:modmenu:$modmenu_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.40.1+1.17")
}

loom {
    runConfigs["client"].runDir = "run/1.17.x"
    runConfigs["client"].programArgs.addAll(listOf<String>("--width=1280", "--height=720", "--username=DEV"))
    //refmapName = "inventoryprofilesnext-refmap.json"
    mixin.defaultRefmapName.set("inventoryprofilesnext-refmap.json")
}

afterEvaluate {
    project.sourceSets.getByName("main") {
        this.java.srcDirs("./src/shared/java")
    }
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
        libraryjars( configurations.runtimeClasspath.get().files.filter {
            !it.name.contains("InventoryProfilesNext-common")
        })
    }
    dependsOn(tasks["shadowJar"])
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

    tasks.register<Copy>("copyJarForPublish") {
        dependsOn(remapped)

        val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapShadedJar").get()
        from(fabricRemapJar.archiveFile.get().asFile)
        into(layout.buildDirectory.dir("publish"))
        rename {
            "$mod_loader-$minecraft_version-$mod_artefact_version.jar"
        }

        logger.lifecycle("will rename ${fabricRemapJar.archiveFile.get().asFile} to $mod_loader-$minecraft_version-$mod_artefact_version.jar" )
    }

}

tasks.named<DefaultTask>("build") {
    dependsOn(tasks["remapShadedJar"])
    dependsOn("copyJavadoc")
    dependsOn("packageSources")
    dependsOn("copyJarForPublish")
}


// ============
// curseforge
// ============



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
        val fabricRemapJar = tasks.named<RemapJarTask>("remapShadedJar").get()
        val remappedJarFile = fabricRemapJar.archiveFile.get().asFile
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


val publishModrinth by tasks.registering(TaskModrinthUpload::class) {

    onlyIf {
        System.getenv("MODRINTH_TOKEN") != null && System.getenv("IPNEXT_RELEASE") != null
    }

    token = System.getenv("MODRINTH_TOKEN") // An environment property called MODRINTH that is your token, set via Gradle CLI, GitHub Actions, Idea Run Configuration, or other

    projectId = "O7RBXm3n"
    versionNumber = "$mod_loader-$minecraft_version-$mod_version" // Will fail if Modrinth has this version already
    // On fabric, use 'remapJar' instead of 'jar'
    this.changelog
    val fabricRemapJar = tasks.named<RemapJarTask>("remapShadedJar").get()
    val remappedJarFile = fabricRemapJar.archiveFile
    uploadFile = remappedJarFile // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
    logger.lifecycle("""
        +*************************************************+
        Will release ${remappedJarFile.get().asFile.path}
        +*************************************************+
    """.trimIndent())
    supported_minecraft_versions.forEach { ver ->
        addGameVersion(ver) // Call this multiple times to add multiple game versions. There are tools that can help you generate the list of versions
    }
    versionName = "IPN $mod_version for $mod_loader $minecraft_version"
    changelog = project.rootDir.resolve("changelog.md").readText()
    addLoader(mod_loader)
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