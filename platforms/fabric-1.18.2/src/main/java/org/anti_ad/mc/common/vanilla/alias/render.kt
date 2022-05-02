package org.anti_ad.mc.common.vanilla.alias

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.ClickEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper

typealias Text = Text
typealias TextSerializer = Text.Serializer
typealias LiteralText = LiteralText
typealias MutableText = MutableText
typealias TranslatableText = TranslatableText

typealias TextRenderer = TextRenderer

typealias DrawableHelper = DrawableHelper

typealias RenderSystem = RenderSystem

typealias DiffuseLighting = DiffuseLighting
typealias GlStateManager = GlStateManager
typealias SrcFactor = GlStateManager.SrcFactor
typealias DstFactor = GlStateManager.DstFactor

typealias MatrixStack = MatrixStack
typealias MathHelper = MathHelper
typealias Style = Style
typealias ClickEvent = ClickEvent
typealias ClickEventAction = ClickEvent.Action
typealias Formatting = Formatting

fun getTranslatable(s: String, vararg args: Any): Text = net.minecraft.text.TranslatableText(s, args)

fun getLiteral(s: String): Text = net.minecraft.text.LiteralText(s)