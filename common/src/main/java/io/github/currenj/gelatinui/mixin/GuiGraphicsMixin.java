package io.github.currenj.gelatinui.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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

    @Override
    public void potions_plus$fillQuad(RenderType renderType,
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
}

