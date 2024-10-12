/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2024 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.integration

import kotlinx.serialization.json.decodeFromStream
import org.anti_ad.mc.common.extensions.trySwallow
import java.io.InputStream
import java.nio.file.Path

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
object SlotIntegrationHints {

    @JvmStatic
    private val DEFAULT_ENABLE: SlotIntegrationData = SlotIntegrationData(false)

    private const val exampleFileName = "exampleIntegrationHints.json"

    private const val builtInHintsResource = "assets/inventoryprofilesnext/config/SlotIntegrationHints.json"

    //private const val hintsExport = "ModIntegrationExport.json"

    //private const val exampleHintsResource = "assets/inventoryprofilesnext/config/$exampleFileName"

    //private const val integratedOverride = "ModIntegrationOverride.json"

    private lateinit var externalHintsPath: Path

    private lateinit var configRoot: Path

    private var hints: MutableMap<String, SlotIntegrationData> = mutableMapOf()


    fun hintFor(className: String): SlotIntegrationData {
        return hints[className] ?: DEFAULT_ENABLE
    }

    fun init(root: Path, external: Path) {
        reset()
        configRoot = root
        externalHintsPath = external
        doInit()
    }

    private fun reset() {
        hints.clear()
    }

    private fun doInit() {
        SlotIntegrationHints::class.java.classLoader.getResourceAsStream(builtInHintsResource)?.use { input ->
            readConfig(input)
        }
    }

    private fun readConfig(input: InputStream) {
        val data = json.decodeFromStream<Map<String, SlotIntegrationData>>(input).also {
            trySwallow { input.close() }
        }
        hints.putAll(data)
    }
}
