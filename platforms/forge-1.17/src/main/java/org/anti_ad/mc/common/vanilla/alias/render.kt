package org.anti_ad.mc.common.vanilla.alias

import com.mojang.blaze3d.vertex.PoseStack //com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiComponent //net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.Font //net.minecraft.client.gui.FontRenderer
import com.mojang.blaze3d.platform.Lighting //net.minecraft.client.renderer.RenderHelper
import net.minecraft.network.chat.Component //net.minecraft.util.text.ITextComponent
import net.minecraft.network.chat.TextComponent //net.minecraft.util.text.StringTextComponent
import net.minecraft.network.chat.TranslatableComponent //net.minecraft.util.text.TranslationTextComponent

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