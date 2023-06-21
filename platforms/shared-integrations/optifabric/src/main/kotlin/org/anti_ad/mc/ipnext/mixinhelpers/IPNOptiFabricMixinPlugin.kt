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

package org.anti_ad.mc.ipnext.mixinhelpers

import net.fabricmc.loader.api.FabricLoader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class IPNOptiFabricMixinPlugin: IMixinConfigPlugin {

    var hasOptiFabric = false

    val vanillaMixin = "org.anti_ad.mc.ipnext.mixin.MixinGameRenderer"

    val optifabricMixin = "org.anti_ad.mc.ipnext.mixin.MixinGameRendererOptifabric"

    override fun onLoad(mixinPackage: String?) {
        hasOptiFabric = FabricLoader.getInstance().getModContainer("optifabric").isPresent
    }

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun shouldApplyMixin(targetClassName: String?,
                                  mixinClassName: String?): Boolean {
        return if (hasOptiFabric) {
            mixinClassName != vanillaMixin
        } else {
            mixinClassName != optifabricMixin
        }
    }

    override fun acceptTargets(myTargets: MutableSet<String>?,
                               otherTargets: MutableSet<String>?) {
    }

    override fun getMixins(): MutableList<String>? {
        return null
    }

    override fun preApply(targetClassName: String?,
                          targetClass: ClassNode?,
                          mixinClassName: String?,
                          mixinInfo: IMixinInfo?) {
    }

    override fun postApply(targetClassName: String?,
                           targetClass: ClassNode?,
                           mixinClassName: String?,
                           mixinInfo: IMixinInfo?) {
    }

}
