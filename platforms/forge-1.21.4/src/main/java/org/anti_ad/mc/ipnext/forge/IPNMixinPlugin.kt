/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2022 Plamen K. Kosseff <p.kosseff@gmail.com>
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
package org.anti_ad.mc.ipnext.forge

import net.minecraftforge.fml.loading.FMLLoader
import org.anti_ad.mc.ipnext.ModInfo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class IPNMixinPlugin: IMixinConfigPlugin {

    private val logger: Logger = LogManager.getLogger("IMixinConfigPlugin")

    override fun onLoad(mixinPackage: String) {}

    override fun getRefMapperConfig(): String? {
        return null
    }

    private var shouldLoad = true

    private var resolved = false

    private fun resolveMyDependencies() {
        if (shouldLoad) {

            logger.warn("${ModInfo.MOD_NAME}: Doing our own dependency resolving! Because Forge is being Forge....: https://github.com/MinecraftForge/MinecraftForge/issues/9088")

            val modFile = FMLLoader.getLoadingModList().getModFileById(ModInfo.MOD_ID)

            if (modFile != null) {
                modFile.mods.find {
                    it.modId == ModInfo.MOD_ID
                }?.dependencies?.forEach {
                    //logger.error("\tFound dependencies: ${it.modId}, mandatory: ${it.isMandatory}")
                    if (it.isMandatory) {
                        val depFile = FMLLoader.getLoadingModList().getModFileById(it.modId)
                        //logger.error("Found depFile: ${depFile.file.fileName}")
                        shouldLoad = depFile != null
                        if (!shouldLoad) {
                            logger.info("${ModInfo.MOD_NAME} dependency resolution failed! Probably missing libIPN.")
                            resolved = true
                            return
                        }
                    }
                }
            } else {
                logger.info("${ModInfo.MOD_NAME} dependency resolution failed! Probably missing libIPN.")
                shouldLoad = false
            }
            resolved = true
        }
    }

    override fun shouldApplyMixin(targetClassName: String,
                                  mixinClassName: String): Boolean {
        if (!resolved) resolveMyDependencies()
        return shouldLoad

    }

    override fun acceptTargets(myTargets: Set<String>,
                               otherTargets: Set<String>) {
    }

    override fun getMixins(): List<String>? {
        return null
    }

    override fun preApply(targetClassName: String,
                          targetClass: ClassNode,
                          mixinClassName: String,
                          mixinInfo: IMixinInfo) {
    }

    override fun postApply(targetClassName: String,
                           targetClass: ClassNode,
                           mixinClassName: String,
                           mixinInfo: IMixinInfo) {
    }
}
