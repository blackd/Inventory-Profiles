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
    //apply(plugin = "kotlin")
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

        maven {
            url = uri("https://masa.dy.fi/maven")
            name = "carpet"
        }
    }

    dependencies {
        "api"("org.jetbrains:annotations:20.1.0")
        "shadedApi"("com.yevdo:jwildcard:1.4")

        val antlrVersion = "4+"
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
                                   modmenu_version: Any? = null,
                                   carpet_version: Any? = null) {

    configurations.all {
        resolutionStrategy {
            force("net.fabricmc:fabric-loader:$loader_version")
            force("org.anti_ad.mc:libIPN-$libIPN_version")
        }
    }

    dependencies {

        "api"(kotlin("stdlib"))
        "api"(kotlin("reflect"))

        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.0")

        "minecraft"("com.mojang:minecraft:$minecraft_version")
        "mappings"("net.fabricmc:yarn:$mappings_version:v2")

        "modImplementation"("net.fabricmc:fabric-loader:$loader_version")
        "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
        modmenu_version?.let {
            "modApi"("com.terraformersmc:modmenu:$modmenu_version")
        }

        libIPN_version?.let {
            "modApi"("org.anti_ad.mc:libIPN-$libIPN_version:dev")  {
                this.isChanging = true
            }
        }
        carpet_version?.let {
            "modCompileOnly"("carpet:fabric-carpet:$carpet_version")
        }

        "modRuntimeOnly"("net.fabricmc:fabric-language-kotlin:1.11.0+kotlin.2.0.0")
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


/*    configurations.all {
        resolutionStrategy {
            force(fgdeobf("org.anti_ad.mc:libIPN-$libIPN_version"))
        }
    }*/


    dependencies {

        "minecraft"("net.minecraftforge:forge:$minecraft_version-$loader_version")

        "implementation"("thedarkcolour:kotlinforforge:$kotlin_for_forge_version") {
            this.isChanging = true
        }

/*
        "runtimeOnly"( "thedarkcolour:kfflang:$kotlin_for_forge_version") {
            exclude("org.jetbrains.kotlin")
            this.isChanging = true
        }
        "runtimeOnly"("thedarkcolour:kfflib:$kotlin_for_forge_version") {
            exclude("org.jetbrains.kotlin")
            this.isChanging = true
        }
        "runtimeOnly"("thedarkcolour:kffmod:$kotlin_for_forge_version") {
            exclude("org.jetbrains.kotlin")
            this.isChanging = true
        }
*/
        "implementation"("org.anti_ad.mc:libIPN-$libIPN_version:dev") {
            exclude("org.jetbrains.kotlin")
            this.isChanging = true
        }

        "compileOnly"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0") {
            exclude("org.jetbrains.kotlin")
            this.isChanging = true
        }
        "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib:2.0.0") {
            exclude("org.jetbrains.kotlin")
        }

        "implementation"("net.sf.jopt-simple:jopt-simple:5.0.4") {
            version {
                strictly("5.0.4")
            }
        }
    }
}
