package io.github.currenj.gelatinui.extension;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Vector2f;

import java.util.Optional;

public interface IGuiGraphicsExtension {
    void gelatinui$fill(RenderPipeline renderPipeline, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int color);

    void gelatinui$fillQuad(RenderPipeline renderPipeline, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color);

    void gelatinui$renderTooltip(Font arg, Optional<TooltipComponent> optional, int i, int j);

    void gelatinui$blit(Identifier resourceLocation, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight);
    void gelatinui$innerBlit(Identifier resourceLocation, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2);
}
