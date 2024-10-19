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

package org.anti_ad.mc.ipnext.integration

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.exists
import org.anti_ad.mc.common.extensions.trySwallow
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipn.api.IPNGuiHint
import org.anti_ad.mc.ipn.api.IPNIgnore
import org.anti_ad.mc.ipn.api.IPNPlayerSideOnly
import org.anti_ad.mc.ipn.api.IPNSlotsIgnoreForInventoryTypes
import java.io.InputStream
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.moveTo
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.outputStream




@OptIn(ExperimentalSerializationApi::class)
object HintsManagerNG {

    private const val exampleFileName = "exampleIntegrationHints.json"

    private const val builtInHintsResource = "assets/inventoryprofilesnext/config/ModIntegrationHintsNG.json"

    private const val hintsExport = "ModIntegrationExport.json"

    private const val exampleHintsResource = "assets/inventoryprofilesnext/config/$exampleFileName"

    private const val integratedOverride = "ModIntegrationOverride.json"

    private lateinit var externalHintsPath: Path

    private lateinit var configRoot: Path

    private val externalConfigs: MutableMap<String, HintClassData> = mutableMapOf()

    private val internalConfigs: MutableMap<String, HintClassData> = mutableMapOf()

    private val effectiveHints: MutableMap<String, HintClassData> = mutableMapOf()

    private fun processConfig(stream: InputStream): Map<String, HintClassData> = json.decodeFromStream<Map<String, HintClassData>>(stream).also {
        trySwallow {
            stream.close()
        }
    }

    fun init(root: Path, external: Path, fromUserInput: Boolean) {
        reset()
        configRoot = root
        externalHintsPath = external
        doInit(fromUserInput)
    }

    private fun doInit(fromUserInput: Boolean) {
        if (externalHintsPath.isDirectory()) {
            Files.find(externalHintsPath, 1, { p, a ->
                a.isRegularFile && p.fileName.toString().endsWith(".json") && p.fileName.toString() != "exampleIntegrationHints.json"
            }, FileVisitOption.FOLLOW_LINKS).forEach { f ->
                val id = f.name.substringBeforeLast(".json")
                tryLog(id, fromUserInput, ::logError) {
                    val data = processConfig(f.inputStream())
                    data.forEach { v ->
                        externalConfigs[v.key] = v.value.also {
                            it.changeId(id)
                            it.fillMissingHints()
                        }
                    }
                }
            }
        }
        val overrideFile = configRoot / integratedOverride
        if (overrideFile.exists()) {
            overrideFile.inputStream().use { input ->
                readInternalConfig(input, fromUserInput)
            }
        } else {
            HintsManagerNG::class.java.classLoader.getResourceAsStream(builtInHintsResource)?.use { input ->
                readInternalConfig(input, fromUserInput)
            }
        }
        Log.info("Loaded ${externalConfigs.size} external GUI hints")
        externalConfigs.forEach { (name, hintClassData) ->
            effectiveHints.putIfAbsent(name, hintClassData)
        }
        Log.info("Loaded ${internalConfigs.size} build-in GUI hints")
        internalConfigs.forEach { (name, hintClassData) ->
            effectiveHints.putIfAbsent(name, hintClassData)
        }
        if (fromUserInput) {
            TellPlayer.chat("Found ${effectiveHints.size} hints. ${internalConfigs.size} internal and ${externalConfigs.size} external")
        }
        Log.info("Effective GUI hints after merge: ${effectiveHints.size}")
    }

    private fun readInternalConfig(input: InputStream, fromUserInput: Boolean) {
        tryLog("", fromUserInput, ::logError) {
            val data: MutableMap<String, Map<String, HintClassData>> = json.decodeFromStream<MutableMap<String, Map<String, HintClassData>>>(
                input).also {
                trySwallow { input.close() }
            }
            data.forEach { ids ->
                ids.value.forEach { v ->
                    internalConfigs[v.key] = v.value.also { it.changeId(ids.key) }
                }
            }
        }
    }

    fun saveAllAsIntegrated(priority: MergePriority) {
        val data: MutableMap<String, MutableMap<String, HintClassData>> = collectAllWithPriority(priority)
        val d1 = data.toSortedMap()
        val file = configRoot / hintsExport
        file.deleteIfExists()
        with(file.outputStream()) {
            json.encodeToStream(MapSerializer(String.serializer(), MapSerializer(String.serializer(), HintClassData.serializer())), d1, this)
            this.close()
        }
    }

    private fun collectAllWithPriority(priority: MergePriority): MutableMap<String, MutableMap<String, HintClassData>> {
        val first = if (priority == MergePriority.EXTERNAL) externalConfigs else internalConfigs
        val second = if (priority == MergePriority.EXTERNAL) internalConfigs else externalConfigs
        val data: MutableMap<String, MutableMap<String, HintClassData>> = mutableMapOf()
        val included = mutableSetOf<String>()
        first.forEach { (key, value) ->
            val dataForId = data.putIfAbsent(value.readId()!!, mutableMapOf()) ?: data[value.readId()!!]
            dataForId!!.putIfAbsent(key, value.copy(buttonHints = value.copyOnlyChanged()))
            included.add(key)
        }
        second.forEach { (key, value) ->
            if (!included.contains(key)) {
                val dataForId = data.putIfAbsent(value.readId()!!, mutableMapOf()) ?: data[value.readId()!!]
                dataForId!!.putIfAbsent(key, value.copy(buttonHints = value.copyOnlyChanged()))
            }
        }
        return data
    }

    fun saveAllAsSeparate(priority: MergePriority) {
        val data: MutableMap<String, MutableMap<String, HintClassData>> = collectAllWithPriority(priority)
        data.forEach { (id, hints) ->
            val file = externalHintsPath / "$id.json"
            file.deleteIfExists()
            with(file.outputStream()) {
                TellPlayer.chat("Generating ${file.name}")
                json.encodeToStream(hints, this)
            }
        }
    }


    fun getHints(cl: Class<*>): HintClassData {
        return effectiveHints[cl.name].let {
            it.let {
                if (it != null && !it.force
                    && (cl.isAnnotationPresent(IPNIgnore::class.java)
                            || cl.isAnnotationPresent(IPNPlayerSideOnly::class.java)
                            || cl.isAnnotationPresent(IPNGuiHint::class.java)
                            || cl.isAnnotationPresent(IPNSlotsIgnoreForInventoryTypes::class.java))) {
                    null
                } else {
                    it
                }
            } ?: run {
                val isIgnored = getIgnoredClass(cl)?.also { ignored ->
                    if (ignored != cl) {
                        getHints(ignored)
                    }
                } != null
                val buttonHints: MutableMap<IPNButton, ButtonPositionHint> = mutableMapOf()
                cl.getAnnotationsByType(IPNGuiHint::class.java).forEach { ipnButton ->
                    buttonHints[ipnButton.button] = ButtonPositionHint(ipnButton.horizontalOffset,
                                                                       ipnButton.top,
                                                                       ipnButton.bottom,
                                                                       ipnButton.hide)
                }

                val isIPNPlayerSideOnly = cl.isAnnotationPresent(IPNPlayerSideOnly::class.java)
                val slotIgnoreForInventoryTypes: MutableSet<String> = cl.getAnnotationsByType(IPNSlotsIgnoreForInventoryTypes::class.java).firstOrNull()?.value?.toMutableSet() ?: mutableSetOf()
                val ignoreCraftingGrid: Boolean = cl.getAnnotationsByType(IPNSlotsIgnoreForInventoryTypes::class.java).firstOrNull()?.ignoreCraftingSlots == true

                val newVal = if ( isIgnored || isIPNPlayerSideOnly || ignoreCraftingGrid || buttonHints.isNotEmpty() || slotIgnoreForInventoryTypes.isNotEmpty()) {
                    HintClassData(ignore = isIgnored,
                                  playerSideOnly = isIPNPlayerSideOnly,
                                  disableFastSwipe =  false,
                                  buttonHints = buttonHints,
                                  slotIgnoreInventoryTypes = slotIgnoreForInventoryTypes,
                                  ignoreCraftingGrid = ignoreCraftingGrid)
                } else {
                    HintClassData()
                }.also { nv ->
                    nv.fillMissingHints()
                }
                effectiveHints[cl.name] = newVal
                newVal
            }
        }

    }

    private fun getIgnoredClass(container: Class<*>): Class<*>? {
        var sup: Class<*> = container
        while (sup != Object::class.java) {
            if (sup.isAnnotationPresent(IPNIgnore::class.java)) {
                return sup
            }
            sup = sup.superclass
        }
        return null
    }

    private fun reset() {
        externalConfigs.clear()
        internalConfigs.clear()
        effectiveHints.clear()
    }

    fun isPlayerSideOnly(javaClass: Class<*>?): Boolean {
        return javaClass != null && getHints(javaClass).playerSideOnly
    }

    fun isFastSwipeDisabled(javaClass: Class<*>?): Boolean {
        return javaClass != null && getHints(javaClass).disableFastSwipe
    }

    fun upgradeOldConfig(oldConfigFile: Path,
                         newConfigDir: Path) {
        if (oldConfigFile.exists()) {
            trySwallow {
                oldConfigFile.moveTo(newConfigDir / "upgraded-From-Pre-v1.2.5.json")
            }
        }
        val example = newConfigDir / exampleFileName
        if (example.notExists()) {
            HintsManagerNG::class.java.classLoader.getResourceAsStream(exampleHintsResource)?.use { input ->
                trySwallow {
                    example.outputStream().use { output ->
                        input.copyTo(output)
                        output.close()
                    }
                    input.close()
                }
            }
        }
    }

    private fun getAllById(id: String): Map<String, HintClassData> {
        return mutableMapOf<String, HintClassData>().also { res ->
            effectiveHints.filterValues {
                id == it.readId()
            }.forEach {
                if (it.value.hasInfo())  {
                    val hints = it.value.copyOnlyChanged()
                    res[it.key] = it.value.copy(buttonHints = hints)
                }
            }
        }
    }

    fun saveDirty(screenHints: HintClassData,
                  containerHints: HintClassData) {

        if (screenHints.readId() == containerHints.readId()) {
            saveFile(screenHints.readId()!!, getAllById(screenHints.readId()!!))
        } else {
            saveFile(screenHints.readId()!!, getAllById(screenHints.readId()!!))
            saveFile(containerHints.readId()!!, getAllById(containerHints.readId()!!))
        }
        reset()
        doInit(false)
    }

    private fun saveFile(readId: String,
                         allById: Map<String, HintClassData>) {
        val fileName = if (!readId.lowercase().endsWith(".json")) {
            "$readId.json"
        } else {
            readId
        }
        val file = externalHintsPath / fileName
        file.deleteIfExists()
        if (allById.isNotEmpty()) json.encodeToStream(allById, file.outputStream())
    }
}


private const val ENUM = "inventoryprofiles.enum"

enum class MergePriority {
    INTERNAL,
    EXTERNAL;

    override fun toString(): String =
            I18n.translate("$ENUM.merge_priority.${name.lowercase()}")
}
