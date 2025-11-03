package io.github.currenj.gelatinui.extension;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Vector2f;

import java.util.Optional;

public interface IGuiGraphicsExtension {
    void gelatinui$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color);

    void gelatinui$fillQuad(RenderType renderType, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int z, int color);

    void gelatinui$renderTooltip(Font arg, Optional<TooltipComponent> optional, int i, int j);

    void gelatinui$blit(ResourceLocation resourceLocation, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight);
    void gelatinui$blit(ResourceLocation resourceLocation, float x1, float x2, float y1, float y2, float z, float n, float o, float f, float g, int p, int q);
    void gelatinui$innerBlit(ResourceLocation resourceLocation, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2);
}
