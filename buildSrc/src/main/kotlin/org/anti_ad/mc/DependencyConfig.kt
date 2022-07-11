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

package org.anti_ad.mc

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.dependencies

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
    }

    repositories {
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
    }

    dependencies {
        "api"("org.jetbrains:annotations:20.1.0")
        "shadedApi"("com.yevdo:jwildcard:1.4")
    }
}

fun Project.fabricCommonDependency(minecraft_version: Any,
                                   mappings_version: Any,
                                   loader_version: Any,
                                   fabric_api_version: Any,
                                   modmenu_version: Any? = null) {
    dependencies {

        "shadedApi"(project(":common"))
        "api"("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.21")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")

        "minecraft"("com.mojang:minecraft:$minecraft_version")
        "mappings"("net.fabricmc:yarn:$mappings_version:v2")

        "modImplementation"("net.fabricmc:fabric-loader:$loader_version")
        "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
        modmenu_version?.let {
            "modImplementation"("com.terraformersmc:modmenu:$modmenu_version")
        }
        "modRuntimeOnly"("net.fabricmc:fabric-language-kotlin:1.8.1+kotlin.1.7.0")
    }
}

fun Project.forgeCommonDependency(minecraft_version: Any,
                                  loader_version: Any,
                                  kotlin_for_forge_version: Any) {
    dependencies {
        "shadedApi"(project(":common"))
        "api"("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.21")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
        "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
        "minecraft"("net.minecraftforge:forge:$minecraft_version-$loader_version")
        "api"("org.spongepowered:mixin:0.8.3-SNAPSHOT")
        "annotationProcessor"("org.spongepowered:mixin:0.8.3-SNAPSHOT:processor")
        "testAnnotationProcessor"("org.spongepowered:mixin:0.8.3-SNAPSHOT:processor")
        "implementation"("thedarkcolour:kotlinforforge:$kotlin_for_forge_version")
    }
}
