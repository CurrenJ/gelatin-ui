package grill24.gelatinui.gui;

import net.minecraft.resources.ResourceLocation;

/**
 * Abstraction layer for rendering operations.
 * Allows the GUI system to be independent of specific rendering implementations.
 */
public interface IRenderContext {
    /**
     * Fill a rectangle with a color.
     * @param x1 Left edge
     * @param y1 Top edge
     * @param x2 Right edge
     * @param y2 Bottom edge
     * @param color ARGB color value
     */
    void fill(int x1, int y1, int x2, int y2, int color);

    /**
     * Draw a string of text.
     * @param text Text to draw
     * @param x X position
     * @param y Y position
     * @param color ARGB color value
     */
    void drawString(String text, int x, int y, int color);

    /**
     * Draw centered text.
     * @param text Text to draw
     * @param x Center X position
     * @param y Y position
     * @param color ARGB color value
     */
    void drawCenteredString(String text, int x, int y, int color);

    /**
     * Get the width of a string in pixels.
     * @param text Text to measure
     * @return Width in pixels
     */
    int getStringWidth(String text);

    /**
     * Get the height of the font.
     * @return Height in pixels
     */
    int getFontHeight();

    /**
     * Push a scissor region for clipping.
     * @param x Left edge
     * @param y Top edge
     * @param width Width
     * @param height Height
     */
    void pushScissor(int x, int y, int width, int height);

    /**
     * Pop the current scissor region.
     */
    void popScissor();

    /**
     * Enable blending for transparency.
     */
    void enableBlend();

    /**
     * Disable blending.
     */
    void disableBlend();

    /**
     * Draw a texture (sprite) at the given local coordinates with the provided size.
     * This is a convenience method that draws the full region of the texture to the requested size.
     * @param texture ResourceLocation of the texture
     * @param x Local X (pixels)
     * @param y Local Y (pixels)
     * @param width Width in pixels
     * @param height Height in pixels
     */
    default void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {
        // Default implementation delegates to the UV-aware method using a full-region copy
        drawTexture(texture, x, y, width, height, 0, 0, width, height, 256, 256);
    }

    /**
     * Draw a sub-region (UV) of a texture to the given local rectangle.
     * Matches GuiGraphics.blit signature for texture rendering.
     * @param texture ResourceLocation of the texture
     * @param x Destination X
     * @param y Destination Y
     * @param width Destination width
     * @param height Destination height
     * @param u Source X in texture pixels
     * @param v Source Y in texture pixels
     * @param texWidth Width of the source region in texture pixels
     * @param texHeight Height of the source region in texture pixels
     * @param textureWidth Total width of the texture atlas
     * @param textureHeight Total height of the texture atlas
     */
    void drawTexture(ResourceLocation texture, int x, int y, int width, int height, int u, int v, int texWidth, int texHeight, int textureWidth, int textureHeight);
}
