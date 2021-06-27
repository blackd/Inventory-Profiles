import org.anti_ad.mc.configureCommon
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.UserDevExtension
import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace
import proguard.gradle.ProGuardTask

buildscript {
    repositories {
        maven { url = uri("https://files.minecraftforge.net/maven") }
        jcenter()
        mavenCentral()
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
    }
    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = "5.0.+")
    }
}
apply(plugin = "net.minecraftforge.gradle")

plugins {
    java
    id("com.modrinth.minotaur").version("1.1.0")
}

configureCommon()

group = "org.anti_ad.mc.forge-1.16"

repositories {
    maven { url = uri("https://files.minecraftforge.net/maven") }
    jcenter()
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
    if (false) {
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
    "minecraft"("net.minecraftforge:forge:$mcVersion-$forgeVersion")
}

if ("true" == System.getProperty("idea.sync.active")) {
    afterEvaluate {
        tasks.withType<JavaCompile>().all {
            options.annotationProcessorPath = files()
        }
    }
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
    finalizedBy(tasks["customJar"])
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
    runs {
        val runConfig = Action<RunConfig> {
            properties(mapOf(
                    //"forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                    "forge.logging.console.level" to "debug"
            ))
            workingDirectory = project.file("run").canonicalPath
            source(sourceSets["main"])
            jvmArg("--add-exports=java.base/sun.security.util=ALL-UNNAMED")
            jvmArg("--add-opens=java.base/java.util.jar=ALL-UNNAMED")
        }
        create("client", runConfig)
        //create("server", runConfig)
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


