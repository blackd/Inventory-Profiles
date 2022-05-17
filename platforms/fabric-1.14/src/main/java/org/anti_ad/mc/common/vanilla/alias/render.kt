package org.anti_ad.mc.common.vanilla.alias

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.util.math.Matrix4f
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.Style
import net.minecraft.text.TranslatableText
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent

import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper


typealias Text = Text
typealias TextSerializer = Text.Serializer
typealias LiteralText = LiteralText
typealias TranslatableText = TranslatableText

typealias TextRenderer = TextRenderer

typealias DrawableHelper = DrawableHelper

typealias RenderSystem = GlStateManager
typealias MatrixStack = Matrix4f
typealias DiffuseLighting = DiffuseLighting
typealias GlStateManager = GlStateManager
typealias SrcFactor = GlStateManager.SourceFactor
typealias DstFactor = GlStateManager.DestFactor
typealias MathHelper = MathHelper
typealias Style = Style
typealias ClickEvent = ClickEvent
typealias ClickEventAction = ClickEvent.Action
typealias Formatting = Formatting

fun getTranslatable(s: String, vararg args: Any): Text = net.minecraft.text.TranslatableText(s, args)

fun getLiteral(s: String): Text = net.minecraft.text.LiteralText(s)
