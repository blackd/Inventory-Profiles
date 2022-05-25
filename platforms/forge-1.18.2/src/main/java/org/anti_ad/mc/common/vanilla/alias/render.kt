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

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.network.chat.MutableComponent

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Style
import net.minecraft.ChatFormatting

import net.minecraft.util.Mth

typealias Text = Component // TextComponent is BaseText!!!!!!!
typealias TextSerializer = Component.Serializer
typealias LiteralText = TextComponent
typealias TranslatableText = TranslatableComponent
typealias MutableText = MutableComponent

typealias TextRenderer = Font

typealias DrawableHelper = GuiComponent

typealias RenderSystem = RenderSystem
typealias MatrixStack = PoseStack
typealias DiffuseLighting = Lighting
typealias GlStateManager = GlStateManager
typealias SrcFactor = GlStateManager.SourceFactor
typealias DstFactor = GlStateManager.DestFactor

typealias MathHelper = Mth

typealias Style = Style
typealias ClickEvent = ClickEvent
typealias ClickEventAction = ClickEvent.Action
typealias Formatting = ChatFormatting

fun getTranslatable(s: String, vararg args: Any): Text = TranslatableText(s, args)

fun getLiteral(s: String): Text = LiteralText(s)
