package io.github.currenj.gelatinui.extension;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Vector2f;

import java.util.Optional;

public interface IGuiGraphicsExtension {
    void gelatinui$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color);

    void gelatinui$fillQuad(RenderType renderType, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int z, int color);

    void gelatinui$renderTooltip(Font arg, Optional<TooltipComponent> optional, int i, int j);
}
