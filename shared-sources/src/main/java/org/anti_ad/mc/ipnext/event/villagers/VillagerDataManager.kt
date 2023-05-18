/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.event.villagers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.anti_ad.mc.common.extensions.exists
import org.anti_ad.mc.common.vanilla.accessors.entity.`(id)`
import org.anti_ad.mc.common.vanilla.alias.village.VillagerProfession
import org.anti_ad.mc.ipnext.config.ModSettings
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.moveTo
import kotlin.io.path.outputStream

import kotlinx.serialization.json.Json as HiddenJson

@OptIn(ExperimentalSerializationApi::class)
object VillagerDataManager {

    private val json = HiddenJson {
        ignoreUnknownKeys = true
        prettyPrint = true
    }



    private val config: Config = Config()

    private lateinit var path: Path;

    private lateinit var configFile: Path;

    private fun add(target: MutableMap<String, MutableList<VillagerTradeData>>,
            key: String,
            data: VillagerTradeData) {
        target.getOrPut(key) {
            mutableListOf()
        }.add(data)
        config.markDirty()
    }

    private fun get(source: MutableMap<String, MutableList<VillagerTradeData>>, key: String): List<VillagerTradeData> {
        return source.getOrPut(key) {
            config.markDirty()
            mutableListOf()
        }.toList()
    }

    private fun remove(from: MutableMap<String, MutableList<VillagerTradeData>>, key: String?, data: VillagerTradeData) {
        key?.let { id ->
            from[id]?.remove(data)
            config.markDirty()
        }

    }

    fun removeGlobal(professionId: String?,
                     villagerTradeData: VillagerTradeData) = remove(config.globalBookmarks, professionId, villagerTradeData)

    fun removeLocal(uuid: String?,
                    villagerTradeData: VillagerTradeData) = remove(config.localBookmarks, uuid, villagerTradeData)

    fun addGlobal(type: String, data: VillagerTradeData) = add(config.globalBookmarks, type, data)

    fun getGlobal(type: String): List<VillagerTradeData> = get(config.globalBookmarks, type)

    fun addLocal(uuid: String, data: VillagerTradeData) = add(config.localBookmarks, uuid, data)

    fun getLocal(uuid: String): List<VillagerTradeData> = get(config.localBookmarks, uuid)

    fun init(path: Path) {
        //if (!ModSettings.ENABLE_VILLAGER_TRADING.booleanValue) return
        config.clear()
        this.path = path
        this.configFile = this.path / "villager-trading-config.json"
        if (configFile.exists()) {
            try {
                with(configFile.inputStream()) {
                    config.copyFrom(json.decodeFromStream(Config.serializer(), this))
                }
            } catch (t: Throwable) {
                configFile.moveTo(this.path / "${System.currentTimeMillis()}-BAD-villager-trading-config.json", StandardCopyOption.ATOMIC_MOVE)
                config.clear()
                createInitialFile()
            }
        } else {
            createInitialFile()
        }
    }

    private fun createInitialFile() {
        config.globalBookmarks[VillagerProfession.ARMORER.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.BUTCHER.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.CARTOGRAPHER.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.CLERIC.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.FARMER.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.FISHERMAN.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.FLETCHER.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.LEATHERWORKER.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.LIBRARIAN.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.MASON.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.SHEPHERD.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.TOOLSMITH.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.WEAPONSMITH.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.NITWIT.`(id)`] = mutableListOf()
        config.globalBookmarks[VillagerProfession.NONE.`(id)`] = mutableListOf()
        config.markDirty()
        save()
    }

    fun saveIfDirty() {
        if (!ModSettings.VILLAGER_TRADING_ENABLE.booleanValue) return
        if (config.isDirty) {
            save()
        }
    }

    private fun save() {
        if (configFile.exists()) {
            configFile.moveTo(this.path / "prev-villager-trading-config.json", StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        }
        with(configFile.outputStream()) {
            json.encodeToStream(Config.serializer(), config.asSanitized(), this)
        }
        config.cleanDirty()
    }


}
