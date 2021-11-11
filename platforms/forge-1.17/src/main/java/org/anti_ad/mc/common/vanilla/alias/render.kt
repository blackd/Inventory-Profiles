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

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Style
import net.minecraft.ChatFormatting

import net.minecraft.util.Mth

typealias Text = Component // TextComponent is BaseText!!!!!!!
typealias LiteralText = TextComponent
typealias TranslatableText = TranslatableComponent

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
