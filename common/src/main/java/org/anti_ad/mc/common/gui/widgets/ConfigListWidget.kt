/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
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

package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.config.CategorizedMultiConfig
import org.anti_ad.mc.common.config.IConfigOption
import org.anti_ad.mc.common.gui.TooltipsManager
import org.anti_ad.mc.common.gui.layout.AnchorStyles
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawCenteredText
import kotlin.math.roundToInt

private const val COLOR_WHITE = -0x1
private const val textY = 6

fun CategorizedMultiConfig.toListWidget(displayNameOf: (String) -> String,
                                        descriptionOf: (String) -> String,
                                        categoryNameOf: (String) -> String): ConfigListWidget =
    ConfigListWidget(displayNameOf,
                     descriptionOf).apply {
        for ((categoryName, configOptions) in categories) {
            val name = categoryNameOf(categoryName)
            when {
                name.isEmpty() -> Unit
                name.isBlank() -> addEntry(CategoryEntry(name))
                name.startsWith("§§vgap:") -> addHeight(name.substring(7).toIntOrNull() ?: 0)
                name.startsWith("§§hide") -> continue
                else -> addCategory(name)
            }
            configOptions.forEach { addConfigOption(it) }
        }
    }

class ConfigListWidget(private val displayNameOf: (String) -> String,
                       private val descriptionOf: (String) -> String,
                       scrollbarSize: Int = 6) :
    AnchoredListWidget(scrollbarSize) {

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        TooltipsManager.renderAll()
    }

    fun addCategory(categoryName: String) {
        addAnchor(categoryName)
        addEntry(CategoryEntry(categoryName))
    }

    fun addConfigOption(configOption: IConfigOption) {
        addEntry(ConfigOptionEntry(configOption))
    }

    fun addHeight(height: Int) {
        addEntry(Entry().apply { this.height = height })
    }

    // ============
    // inner classes
    // ============
    inner class ConfigOptionEntry(val configOption: IConfigOption) : Entry() {
        val displayName
            get() = displayNameOf(configOption.key)
        val description
            get() = descriptionOf(configOption.key)

        init {
            height = when(configOption.importance) {
                IConfigOption.Importance.IMPORTANT -> 20
                IConfigOption.Importance.NORMAL -> 14
            }
        }

        val configWidget: ConfigWidgetBase<*> = configOption.toConfigWidget().apply {
            anchor = AnchorStyles.all
            this@ConfigOptionEntry.addChild(this)
            top = 0
            left = (this@ConfigOptionEntry.width / 2.5).roundToInt()
            right = 0
            bottom = 0
            active = !configOption.hidden
        }

        val displayNameTextWidget = TextButtonWidget(displayName).apply {
            clickThrough = true
            this@ConfigOptionEntry.addChild(this)
            top = when (configOption.importance) {
                IConfigOption.Importance.IMPORTANT -> textY
                IConfigOption.Importance.NORMAL -> 3
            }
            left = 2
            zIndex = 1
        }

        override fun render(mouseX: Int,
                            mouseY: Int,
                            partialTicks: Float) {
            if (outOfContainer) return
            super.render(mouseX,
                         mouseY,
                         partialTicks)
            if (description.isNotEmpty()
                && displayNameTextWidget.contains(mouseX,
                                                  mouseY)
                && !anchorHeader.contains(mouseX,
                                          mouseY)
            ) {
                TooltipsManager.addTooltip(description,
                                           mouseX,
                                           mouseY,
                                           glue_rScreenWidth * 2 / 3)
            }
        }

        init {
            height = configWidget.height + 1
            sizeChanged += {
                configWidget.left = (width * .60).roundToInt()
            }
        }
    }

    inner class CategoryEntry(private val categoryName: String) : Entry() {
        override fun render(mouseX: Int,
                            mouseY: Int,
                            partialTicks: Float) {
            if (outOfContainer) return
            rDrawCenteredText(categoryName,
                              absoluteBounds,
                              COLOR_WHITE)
        }
    }

    open inner class Entry : Widget() {
        init {
            height = 20
        }

        val outOfContainer: Boolean
            get() = isOutOfContainer(this)
    }
}
