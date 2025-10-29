package io.github.currenj.gelatinui.gui.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.currenj.gelatinui.gui.IRenderContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

/**
 * Minecraft-specific implementation of IRenderContext.
 * Adapts Minecraft's GuiGraphics to the GUI system's rendering interface.
 */
public class MinecraftRenderContext implements IRenderContext {
    private final GuiGraphics graphics;
    private final Font font;

    public MinecraftRenderContext(GuiGraphics graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }

    @Override
    public void fill(int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y2, color);
    }

    @Override
    public void drawString(String text, int x, int y, int color) {
        graphics.drawString(font, text, x, y, color, false);
    }

    @Override
    public void drawCenteredString(String text, int x, int y, int color) {
        graphics.drawCenteredString(font, text, x, y, color);
    }

    @Override
    public int getStringWidth(String text) {
        return font.width(text);
    }

    @Override
    public int getFontHeight() {
        return font.lineHeight;
    }

    @Override
    public void pushScissor(int x, int y, int width, int height) {
        graphics.enableScissor(x, y, x + width, y + height);
    }

    @Override
    public void popScissor() {
        graphics.disableScissor();
    }

    @Override
    public void enableBlend() {
        RenderSystem.enableBlend();
    }

    @Override
    public void disableBlend() {
        RenderSystem.disableBlend();
    }

    @Override
    public void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {
        graphics.blit(texture, x, y, 0, 0, 0, width, height, width, height);
    }

    @Override
    public void drawTexture(ResourceLocation texture, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        // GuiGraphics.blit signature: blit(ResourceLocation, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight)
        // where width/height are destination size, and textureWidth/textureHeight are the total atlas dimensions
        // Note: texWidth and texHeight (source region size) are implicitly the same as width and height when using this blit overload
        // So we need to use the innerBlit method instead for proper UV mapping
        graphics.blit(texture, x, y, width, height, (float) u, (float) v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    /**
     * Get the underlying GuiGraphics for advanced rendering operations.
     */
    public GuiGraphics getGraphics() {
        return graphics;
    }

    /**
     * Get the Font for text rendering operations.
     */
    public Font getFont() {
        return font;
    }
}
