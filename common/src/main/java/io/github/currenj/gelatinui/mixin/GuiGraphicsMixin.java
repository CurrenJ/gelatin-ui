package io.github.currenj.gelatinui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import io.github.currenj.gelatinui.tooltip.ItemStacksInfo;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements IGuiGraphicsExtension {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract void flushIfUnmanaged();

    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;

    @Shadow public abstract void renderTooltip(Font arg, List<Component> list2, Optional<TooltipComponent> optional, int i, int j);

    @Shadow protected abstract void renderTooltipInternal(Font arg, List<ClientTooltipComponent> list, int m, int n, ClientTooltipPositioner arg2);

    /**
     * Our own fillRect implementation that isn't restricted to axis-aligned rectangles.
     */
    @Override
    public void gelatinui$fillQuad(RenderType renderType,
                                   float x1, float y1,
                                   float x2, float y2,
                                   float x3, float y3,
                                   float x4, float y4,
                                   int z, int color) {
        Matrix4f matrix4f = this.pose.last().pose();
        VertexConsumer vertexconsumer = this.bufferSource.getBuffer(renderType);
        // Change vertex order to clockwise: TL, BL, BR, TR
        vertexconsumer.addVertex(matrix4f, x1, y1, (float) z).setColor(color);
        vertexconsumer.addVertex(matrix4f, x4, y4, (float) z).setColor(color);
        vertexconsumer.addVertex(matrix4f, x3, y3, (float) z).setColor(color);
        vertexconsumer.addVertex(matrix4f, x2, y2, (float) z).setColor(color);
        this.flushIfUnmanaged();
    }

    /**
     * Redirects tooltip rendering for Gelatin's custom ItemStacksTooltip.
     */
    @Inject(method = "renderComponentHoverEffect", at = @At(value = "HEAD"))
    public void redirectRenderTooltipForText(Font font, Style style, int i, int j, CallbackInfo ci) {
        if (style != null && style.getHoverEvent() != null)
        {
            HoverEvent hoverEvent = style.getHoverEvent();
            if (hoverEvent.getValue(ItemStacksTooltip.SHOW_ITEM_STACKS) instanceof ItemStacksInfo itemStacksInfo) {
                ItemStacksTooltip tooltipComponent = new ItemStacksTooltip(
                        itemStacksInfo.getItemStacks(),
                        false
                );
                gelatinui$renderTooltip(font, Optional.of(tooltipComponent), i, j);
            }
        }
    }

    /**
     * Our own renderTooltip implementation.
     * NeoForge's implementation of gatherTooltipComponents has a bug.
     * It attempts to insert at index 1 always (magic number),
     * and this will fail if an empty text list is passed.
     * This override takes no text list, and we bypass NeoForge's broken logic entirely.
     */
    @Override
    public void gelatinui$renderTooltip(Font font, Optional<TooltipComponent> tooltipComponent, int i, int j) {
        List<ClientTooltipComponent> list = new ArrayList<>();
        tooltipComponent.ifPresent(t -> list.addFirst(ClientTooltipComponent.create(t)));
        this.renderTooltipInternal(font, list, i, j, DefaultTooltipPositioner.INSTANCE);
    }

    @Override
    public void gelatinui$blit(ResourceLocation resourceLocation, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight) {
        this.gelatinui$blit(resourceLocation, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    @Override
    public void gelatinui$blit(ResourceLocation resourceLocation, float x1, float x2, float y1, float y2, float z, float regionWidth, float regionHeight, float f, float g, int p, int q) {
        this.gelatinui$innerBlit(resourceLocation, x1, x2, y1, y2, z, (f + 0.0F) / p, (f + regionWidth) / p, (g + 0.0F) / q, (g + regionHeight) / q);
    }

    @Override
    public void gelatinui$innerBlit(ResourceLocation resourceLocation, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = this.pose.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, x1, y1, z).setUv(u1, v1);
        bufferBuilder.addVertex(matrix4f, x1, y2, z).setUv(u1, v2);
        bufferBuilder.addVertex(matrix4f, x2, y2, z).setUv(u2, v2);
        bufferBuilder.addVertex(matrix4f, x2, y1, z).setUv(u2, v1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }
}
