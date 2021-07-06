import org.anti_ad.mc.configureCommon
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.UserDevExtension
import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace
import org.spongepowered.asm.gradle.plugins.MixinExtension
import proguard.gradle.ProGuardTask


import com.modrinth.minotaur.TaskModrinthUpload;

val supported_minecraft_versions = listOf("1.16.5")
val mod_loader = "forge"
val mod_version = project.version
val minecraft_version = "1.16.5"

logger.lifecycle("""
    ***************************************************
    Processing "${project.path}"
    supported versions: $supported_minecraft_versions
    loader: $mod_loader
    mod version: $mod_version
    building agains MC: $minecraft_version
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
        classpath(group = "org.spongepowered", name = "mixingradle", version = "0.8.1-SNAPSHOT")
    }
}



configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(30, "seconds")
}


apply(plugin = "net.minecraftforge.gradle")
apply(plugin = "org.spongepowered.mixin")



plugins {
    java
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.modrinth.minotaur") version "1.2.1"
}

configureCommon()

group = "org.anti_ad.mc.forge-1.16"

repositories {
    maven { url = uri("https://maven.minecraftforge.net/maven") }
    mavenCentral()
    maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
}

val forgeVersion = "36.1.32"
val mcVersion = "1.16.5"
dependencies {
    "shadedApi"(project(":common"))
    "implementation"("org.apache.commons:commons-rng-core:1.3")
    "implementation"("commons-io:commons-io:2.4")
    "implementation"("org.apache.commons:commons-lang3:3.8.1")
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib")
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-common")
    if (true) {
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
    "minecraft"("net.minecraftforge:forge:$mcVersion-$forgeVersion")
    "annotationProcessor"("org.spongepowered:mixin:0.8.3-SNAPSHOT:processor")
}

if ("true" == System.getProperty("idea.sync.active")) {
    afterEvaluate {
        tasks.withType<JavaCompile>().all {
            options.annotationProcessorPath = files()
        }
    }
}




tasks.register<Copy>("copyMixinMappings") {
    val inName = layout.buildDirectory.file("tmp/compileJava/mixin.refmap.json")
    val outName = layout.buildDirectory.file("resources/main/")
    from (inName)
    into (outName)
}


tasks.jar {
    manifest {
        attributes(mapOf(
            "MixinConfigs" to "mixins.ipnext.json"
        ))
    }
    dependsOn("copyMixinMappings")
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
    from(
        inName
    )
    rename {
        outName
    }
    into(layout.buildDirectory.dir("libs"))
}

val proguard by tasks.registering(ProGuardTask::class) {

    configuration("../../proguard.txt")

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
        libraryjars( configurations.runtimeClasspath.get().files.filter {
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
    //finalizedBy(tasks["customJar"])
}


afterEvaluate {
    tasks.named<RenameJarInPlace>("reobfJar") {
        var shadow = tasks.getByName("customJar");
        dependsOn(shadow)
        dependsOn(tasks["copyProGuardJar"])
        //input = shadow.archiveFile.orNull?.asFile
    }
    tasks.named<ProGuardTask>("proguard") {
        var shadow = tasks.getByName<ShadowJar>("shadowJar");
        dependsOn(shadow)
    }
}

configure<UserDevExtension> {
    mappings(mapOf(
            "channel" to "snapshot",
            "version" to "20210309-1.16.5"
    ))
    var rcltName = ""
    runs {
        val runConfig = Action<RunConfig> {
            properties(mapOf(
                    //"forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                    "forge.logging.console.level" to "debug",
                    "mixin.env.remapRefMap" to "true",
                    "mixin.env.refMapRemappingFile" to "${projectDir}/build/createSrgToMcp/output.srg",
                    "mixin.debug.verbose" to "true",
                    "mixin.debug.export" to "true",
                    "mixin.debug.dumpTargetOnFailure" to "true"
            ))
            arg("-mixin.config=mixins.ipnext.json")
            workingDirectory = project.file("run").canonicalPath
            source(sourceSets["main"])

            if (sourceSets.findByName("assetsFixtemp") == null) {
                sourceSets.create("assetsFixtemp") {
                    project(":common").layout.buildDirectory.dir("resources/main")
                }
            }
            this.sources.add(sourceSets["assetsFixtemp"])

            jvmArg("--add-exports=java.base/sun.security.util=ALL-UNNAMED")
            jvmArg("--add-opens=java.base/java.util.jar=ALL-UNNAMED")
            //taskName = "plamenRunClient"
            this.forceExit = false
        }
        val action = create("client", runConfig)
        rcltName = action.taskName
        //create("server", runConfig)
        //create("data", runConfig)
    }
    //tasks[rcltName].dependsOn("injectCommonResources")
    //tasks[rcltName].finalizedBy("injectCommonResources")
}


tasks.register<Copy>("injectCommonResources") {
    tasks["prepareRuns"].dependsOn("injectCommonResources")
    from(project(":common").layout.buildDirectory.dir("resources/main"))
    include("assets/**")
    into(project.layout.buildDirectory.dir("resources/main"))
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
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
    outgoing.artifact(tasks.named("deobfJar"))
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.addVariantsFromConfiguration(deobfElements.get()) {
    mapToMavenScope("runtime")
}




configure<com.matthewprenger.cursegradle.CurseExtension> {

    if (System.getenv("CURSEFORGE_DEPOY_TOKEN") != null) {
        apiKey = System.getenv("CURSEFORGE_DEPOY_TOKEN")
    }

    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "495267"
        changelogType = "markdown"
        changelog = file("../../changelog.md")
        releaseType = "beta"
        supported_minecraft_versions.forEach {
            if (!it.toLowerCase().contains("pre") && !it.toLowerCase().contains("shanpshot")) {
                this.addGameVersion(it)
            }
        }
        val forgeReobfJar = tasks.named<Jar>("deobfJar").get()
        val remappedJarFile = forgeReobfJar.archiveFile.get().asFile
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
        System.getenv("MODRINTH_TOKEN") != null
    }

    token = System.getenv("MODRINTH_TOKEN") // An environment property called MODRINTH that is your token, set via Gradle CLI, GitHub Actions, Idea Run Configuration, or other

    projectId = "O7RBXm3n"
    versionNumber = "Inventory Profiles Next-$mod_loader-$minecraft_version-$mod_version" // Will fail if Modrinth has this version already
    // On fabric, use 'remapJar' instead of 'jar'
    this.changelog

    val forgeReobfJar = tasks.named<Jar>("deobfJar").get()
    val remappedJarFile = forgeReobfJar.archiveFile
    uploadFile = remappedJarFile // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
    supported_minecraft_versions.forEach { ver ->
        addGameVersion(ver) // Call this multiple times to add multiple game versions. There are tools that can help you generate the list of versions
    }
    versionName = "Inventory Profiles Next-$mod_loader-$minecraft_version-$mod_version"
    changelog = project.rootDir.resolve("changelog.md").readText()
    logger.lifecycle("""
        ***********************************************************
        $changelog
        ***********************************************************
    """.trimIndent())
    addLoader(mod_loader)

}