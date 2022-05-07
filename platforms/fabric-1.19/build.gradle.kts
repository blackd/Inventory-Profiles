import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.anti_ad.mc.configureCommon
import org.anti_ad.mc.platformsCommonConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import proguard.gradle.ProGuardTask
import com.modrinth.minotaur.dependencies.ModDependency

val supported_minecraft_versions = listOf("1.19")
val mod_loader = "fabric"
val mod_version = project.version.toString()
val minecraft_version = "22w18a"
val mappings_version = "22w18a+build.5"
val loader_version = "0.14.4"
val modmenu_version = "4.0.0-beta.4"
val fabric_api_version = "0.52.1+1.19"

val mod_artefact_version = project.ext["mod_artefact_version"]


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
    loom version: $loom_version
    fabric api version: $fabric_api_version
    ***************************************************
    """.trimIndent())


plugins {
    kotlin("jvm") //version "1.6.21"
    kotlin("plugin.serialization") //version "1.6.21"
    `java-library`
    `maven-publish`
    signing
    id("fabric-loom") //version(loom_version)
    antlr
    id("com.matthewprenger.cursegradle") //version "1.4.0"
    id("com.modrinth.minotaur") //version "2.0.0"
    id("com.github.johnrengelman.shadow")
}

configureCommon()
platformsCommonConfig()

group = "org.anti-ad.mc"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.6"
    jvmTarget = "17"
}

repositories {
    maven {
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup ("curse.maven")
        }
    }
}



dependencies {
    "shadedApi"(project(":common"))
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.21")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")

    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$mappings_version:v2")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("com.terraformersmc:modmenu:$modmenu_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    //modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:7.1.357")

    //modRuntimeOnly("curse.maven:inventorio-491073:3553574")
    //modRuntimeOnly("curse.maven:iron-furnaces-fabric-318036:3556167")
    //modRuntimeOnly("net.fabricmc:fabric-language-kotlin:1.7.1+kotlin.1.6.10")


}

loom {
    runConfigs["client"].runDir = "run/1.19"
    runConfigs["client"].programArgs.addAll(listOf<String>("--width=1280", "--height=720", "--username=DEV"))
    //refmapName = "inventoryprofilesnext-refmap.json"
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
    //dependsOn("prepareRemapShadedJar")
    input.set( File("build/libs/${shadowJar.archiveBaseName.get()}-all-proguard.jar"))
    archiveFileName.set(shadowJar.archiveFileName.get().replace(Regex("-shaded\\.jar$"), ".jar"))
    addNestedDependencies.set(true)
    //addDefaultNestedDependencies.set(false)
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
/*
    val prepareJarRemapTask = tasks.named<net.fabricmc.loom.task.PrepareJarRemapTask>("PrepareJarRemapTask") {
        val proGuardTask = tasks.getByName<ProGuardTask>("proguard")
        val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
        dependsOn(proGuardTask)
        this.inputFile.set(File("build/libs/${shadowJar.archiveBaseName.get()}-all-proguard.jar"))
    }
*/
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

    tasks.named<net.fabricmc.loom.task.PrepareJarRemapTask>("prepareRemapShadedJar") {
        val proGuardTask = tasks.getByName<ProGuardTask>("proguard")
        val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
        dependsOn(proGuardTask)
        this.inputFile.set(File("build/libs/${shadowJar.archiveBaseName.get()}-all-proguard.jar"))
        dependsOn(proGuardTask)
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
        changelog = file("../../description/out/pandoc-release_notes.md")
        releaseType = "release"
        supported_minecraft_versions.forEach {
            val l = it.toLowerCase()
            if (!l.contains("pre") && !l.contains("rc")) {
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
        debug = true
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
    val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapShadedJar").get()
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
            ModDependency("P7dR8mSH", "required"),
            ModDependency("mOgUt4GM", "optional")))
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