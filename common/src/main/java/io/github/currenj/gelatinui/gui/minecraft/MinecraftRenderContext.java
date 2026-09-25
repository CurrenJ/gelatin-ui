package io.github.currenj.gelatinui.gui.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import io.github.currenj.gelatinui.gui.IRenderContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Minecraft-specific implementation of IRenderContext.
 * Adapts Minecraft's GuiGraphics to the GUI system's rendering interface.
 */
public class MinecraftRenderContext implements IRenderContext {
    private final GuiGraphics graphics;
    private final Font font;

    // Mirror of the clip rectangles pushed through this context. Vanilla keeps the authoritative
    // stack inside GuiGraphics, but its accessor is not reachable from mod code, and
    // picture-in-picture render states have to carry their clip rectangle with them (see
    // HiResItems) — so the pushes made here are tracked alongside, the same way vanilla does it.
    private final Deque<ScreenRectangle> scissorMirror = new ArrayDeque<>();

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

        // 1.21.1 GuiGraphics#enableScissor clips to the rectangle as given (it does not apply the pose).
        ScreenRectangle rectangle = new ScreenRectangle(x, y, width, height);
        ScreenRectangle previous = scissorMirror.peekLast();
        if (previous != null) {
            ScreenRectangle intersection = rectangle.intersection(previous);
            rectangle = intersection != null ? intersection : ScreenRectangle.empty();
        }
        scissorMirror.addLast(rectangle);
    }

    @Override
    public void popScissor() {
        graphics.disableScissor();
        scissorMirror.pollLast();
    }

    // 1.21.1's GUI pose is a real 3D PoseStack, so a child's z offset can be honoured by
    // translating it. Pushed/popped around the child's render by UIContainer#renderChildren.
    @Override
    public void pushZOffset(float zOffset) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0f, 0.0f, zOffset);
    }

    @Override
    public void popZOffset() {
        graphics.pose().popPose();
    }

    /** The clip rectangle currently in force, in screen pixels, or null when nothing is clipped. */
    @Nullable
    public ScreenRectangle peekScissor() {
        return scissorMirror.peekLast();
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
    public void drawTexture(ResourceLocation texture, float x, float y, int width, int height) {
        drawTexture(texture, x, y, width, height, 0, 0, width, height, width, height);
    }

    @Override
    public void drawTexture(ResourceLocation texture, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight) {
        // GuiGraphics.blit signature: blit(ResourceLocation, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight)
        // where width/height are destination size, and textureWidth/textureHeight are the total atlas dimensions
        // Note: texWidth and texHeight (source region size) are implicitly the same as width and height when using this blit overload
        // So we need to use the innerBlit method instead for proper UV mapping
        IGuiGraphicsExtension ext = (IGuiGraphicsExtension) graphics;
        ext.gelatinui$blit(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
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
