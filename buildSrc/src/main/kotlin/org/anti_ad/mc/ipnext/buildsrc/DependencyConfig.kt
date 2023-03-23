/*
 * Inventory Profiles Next
 *
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

package org.anti_ad.mc.ipnext.buildsrc

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.dependencies
import java.util.concurrent.*

//var shadedApi: Configuration? = null

fun Project.configureDependencies() {
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "java-library")



    configurations {
        val shaded = create("shaded")
        val shadedApi = create("shadedApi")
        shaded.extendsFrom(shadedApi)
        getByName("api").extendsFrom(shadedApi)
        val shadedImplementation = create("shadedImplementation")
        shaded.extendsFrom(shadedImplementation)
        getByName("implementation").extendsFrom(shadedImplementation)
        val runHelperApi = create("runHelperApi")
        runHelperApi.extendsFrom(getByName("api"))
        //getByName("modApi").extendsFrom(shadedApi)
    }

    repositories {

        maven {
            name = "libIPN-Snapshots"
            this.mavenContent {
                this.snapshotsOnly()
            }
            url = uri("https://maven.ipn-mod.org/snapshots")
        }
        maven {
            name = "libIPN-Releases"
            this.mavenContent {
                this.releasesOnly()
            }
            url = uri("https://maven.ipn-mod.org/releases")
        }

        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.enginehub.org/repo/") }
        maven { url = uri("https://repo.codemc.org/repository/maven-public") }
        //maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
        //maven("https://raw.githubusercontent.com/TerraformersMC/Archive/main/releases")
        maven { url = uri("https://maven.terraformersmc.com/releases") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/releases/") }
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.shedaniel.me") }

        maven {
            url = uri("https://www.cursemaven.com")
            content {
                includeGroup ("curse.maven")
            }
        }
    }

    dependencies {
        "api"("org.jetbrains:annotations:20.1.0")
        "shadedApi"("com.yevdo:jwildcard:1.4")

        val antlrVersion = "4.9.3"
        "antlr"("org.antlr:antlr4:$antlrVersion")
        "shadedApi"("org.antlr:antlr4-runtime:$antlrVersion")


    }

    configurations.all {
        resolutionStrategy {
            cacheChangingModulesFor(0, TimeUnit.SECONDS)
        }
    }
}

fun Project.fabricCommonDependency(minecraft_version: Any,
                                   mappings_version: Any,
                                   loader_version: Any,
                                   fabric_api_version: Any,
                                   libIPN_version: Any? = null,
                                   modmenu_version: Any? = null) {

    configurations.all {
        resolutionStrategy {
            force("net.fabricmc:fabric-loader:$loader_version")
            force("org.anti_ad.mc:libIPN-$libIPN_version")
        }
    }

    dependencies {

        "api"("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.10")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
        "api"(kotlin("reflect"))
        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")

        "minecraft"("com.mojang:minecraft:$minecraft_version")
        "mappings"("net.fabricmc:yarn:$mappings_version:v2")

        "modImplementation"("net.fabricmc:fabric-loader:$loader_version")
        "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
        modmenu_version?.let {
            "modImplementation"("com.terraformersmc:modmenu:$modmenu_version")
        }

        libIPN_version?.let {
            "modApi"("org.anti_ad.mc:libIPN-$libIPN_version")  {
                this.isChanging = true
            }
        }

        "modRuntimeOnly"("net.fabricmc:fabric-language-kotlin:1.9.2+kotlin.1.8.10")
    }

}

private fun ___fgdeobf(id: Any): Dependency {
    TODO()
}

private var __fgdeobf: (Any) -> Dependency = ::___fgdeobf

var Project.fgdeobf: (Any) -> Dependency
    get() = __fgdeobf
    set(value) {
        __fgdeobf = value
    }

fun Project.forgeCommonDependency(minecraft_version: Any,
                                  loader_version: Any,
                                  kotlin_for_forge_version: Any,
                                  libIPN_version: Any?) {

    dependencies {

        "api"(fgdeobf("org.anti_ad.mc:libIPN-$libIPN_version"))
        val kffverstr = kotlin_for_forge_version.toString()[0]

        if (kffverstr == '4' || kffverstr == '3') {
            "compileOnlyApi"("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
            "compileOnlyApi"("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.10")
            "compileOnlyApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10")
            "compileOnlyApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
            "compileOnlyApi"(kotlin("reflect"))
            "compileOnlyApi"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
            "compileOnlyApi"("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")
        } else if (kffverstr == '1') {
            "api"("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
            "api"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.21")
            "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
            "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
            "api"(kotlin("reflect"))
            "api"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
        } else {
            "api"("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
            "api"("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.10")
            "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10")
            "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
            "api"(kotlin("reflect"))
            "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
            "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")

            "runHelperApi"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
            "runHelperApi"("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
            "runHelperApi"("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.10")
            "runHelperApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10")
            "runHelperApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
            "runHelperApi"("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
        }

        "minecraft"("net.minecraftforge:forge:$minecraft_version-$loader_version")
        "api"("org.spongepowered:mixin:0.8.3-SNAPSHOT")
        "annotationProcessor"("org.spongepowered:mixin:0.8.3-SNAPSHOT:processor")
        "testAnnotationProcessor"("org.spongepowered:mixin:0.8.3-SNAPSHOT:processor")
        "implementation"("thedarkcolour:kotlinforforge:$kotlin_for_forge_version") {
            this.isChanging = true
        }



        //these are here, so we add them during the runClient/Server
        //for some reason they are not added by any of the default api/implementation...




    }
}
