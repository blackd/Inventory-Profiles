package io.github.jsnimda.common.vanilla.alias

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent

typealias Text = ITextComponent // TextComponent is BaseText!!!!!!!
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

