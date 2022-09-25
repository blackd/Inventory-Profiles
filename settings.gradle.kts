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


rootProject.name = "InventoryProfilesNext"


if (JavaVersion.current() >= JavaVersion.VERSION_16) {
    /*
    include("lib:platforms:fabric-1.19")
    include("lib:platforms:fabric-1.18.2")
    include("lib:platforms:fabric-1.18")
    include("lib:platforms:fabric-1.17")
    include("lib:platforms:fabric-1.16")
    include("lib:platforms:fabric-1.15")

    include("lib:platforms:forge-1.19")
    include("lib:platforms:forge-1.18.2")
    include("lib:platforms:forge-1.18")
    include("lib:platforms:forge-1.17")
    include("lib:platforms:forge-1.16")
    include("lib:platforms:forge-1.15")
    include("lib:platforms:forge-1.14")
*/

    include("platforms:fabric-1.19")
    include("platforms:fabric-1.18.2")
    include("platforms:fabric-1.18")
    include("platforms:fabric-1.17")
    include("platforms:fabric-1.16")
    include("platforms:fabric-1.15")
    include("platforms:fabric-1.14")
    include("platforms:forge-1.19")
    include("platforms:forge-1.18.2")
    include("platforms:forge-1.18")
    include("platforms:forge-1.17")
    include("platforms:forge-1.16")
    include("platforms:forge-1.15")
    include("platforms:forge-1.14")
}

pluginManagement {
    repositories {
        maven(url = "https://maven.fabricmc.net") {
            name = "Fabric"
        }
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.4.1"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
