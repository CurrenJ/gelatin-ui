package io.github.currenj.gelatinui.gui.minecraft;

import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import io.github.currenj.gelatinui.gui.IRenderContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Minecraft-specific implementation of IRenderContext.
 * Adapts Minecraft's GuiGraphicsExtractor to the GUI system's rendering interface.
 */
public class MinecraftRenderContext implements IRenderContext {
    private final GuiGraphicsExtractor graphics;
    private final Font font;

    public MinecraftRenderContext(GuiGraphicsExtractor graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }

    @Override
    public void fill(int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y2, color);
    }

    @Override
    public void drawString(String text, int x, int y, int color) {
        graphics.text(font, text, x, y, color, false);
    }

    @Override
    public void drawCenteredString(String text, int x, int y, int color) {
        graphics.centeredText(font, text, x, y, color);
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
    public List<String> wrapText(String text, int maxWidth) {
        if (text == null || text.isEmpty() || maxWidth <= 0) {
            return List.of();
        }

        // Use Minecraft's font splitter which handles word-boundary wrapping
        List<FormattedCharSequence> lines = font.split(Component.literal(text), maxWidth);

        // Extract plain text from each FormattedCharSequence line
        List<String> result = new ArrayList<>(lines.size());
        for (FormattedCharSequence line : lines) {
            StringBuilder sb = new StringBuilder();
            line.accept((charIndex, style, codePoint) -> {
                sb.appendCodePoint(codePoint);
                return true;
            });
            result.add(sb.toString());
        }
        return result;
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
        // Blend mode is now controlled per RenderPipeline in 26.1; no-op here
    }

    @Override
    public void disableBlend() {
        // Blend mode is now controlled per RenderPipeline in 26.1; no-op here
    }

    @Override
    public void drawTexture(Identifier texture, float x, float y, int width, int height) {
        drawTexture(texture, x, y, width, height, 0, 0, width, height, width, height);
    }

    @Override
    public void drawTexture(Identifier texture, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight) {
        IGuiGraphicsExtension ext = (IGuiGraphicsExtension) graphics;
        ext.gelatinui$blit(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    /**
     * Get the underlying GuiGraphicsExtractor for advanced rendering operations.
     */
    public GuiGraphicsExtractor getGraphics() {
        return graphics;
    }

    /**
     * Get the Font for text rendering operations.
     */
    public Font getFont() {
        return font;
    }
}
