package io.github.currenj.gelatinui.extension;

import net.minecraft.client.renderer.RenderType;
import org.joml.Vector2f;

public interface IGuiGraphicsExtension {
    void potions_plus$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color);
    void potions_plus$fillQuad(RenderType renderType, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int z, int color);
}
