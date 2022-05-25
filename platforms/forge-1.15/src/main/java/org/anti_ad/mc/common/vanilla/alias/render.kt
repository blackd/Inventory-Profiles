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

package org.anti_ad.mc.common.vanilla.alias

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.Style
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.util.text.TextFormatting



typealias Text = ITextComponent // TextComponent is BaseText!!!!!!!
typealias TextSerializer = ITextComponent.Serializer
typealias LiteralText = StringTextComponent
typealias TranslatableText = TranslationTextComponent

typealias TextRenderer = FontRenderer

typealias DrawableHelper = AbstractGui

typealias RenderSystem = RenderSystem
typealias MatrixStack = MatrixStack
typealias DiffuseLighting = RenderHelper
typealias GlStateManager = GlStateManager
typealias SrcFactor = GlStateManager.SourceFactor
typealias DstFactor = GlStateManager.DestFactor

typealias MathHelper = MathHelper
typealias Style = Style
typealias ClickEvent = ClickEvent
typealias ClickEventAction = ClickEvent.Action
typealias Formatting = TextFormatting

fun getTranslatable(s: String, vararg args: Any): Text = TranslatableText(s, args)

fun getLiteral(s: String): Text = LiteralText(s)
