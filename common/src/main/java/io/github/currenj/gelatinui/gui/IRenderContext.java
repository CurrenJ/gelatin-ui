package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.components.SlicedSpriteData;
import io.github.currenj.gelatinui.gui.components.SpriteData;
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
     * @param regionWidth Width of the source region in texture pixels
     * @param regionHeight Height of the source region in texture pixels
     * @param textureWidth Total width of the texture atlas
     * @param textureHeight Total height of the texture atlas
     */
    void drawTexture(ResourceLocation texture, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight);

    /**
     * Draw a 9-slice sprite that scales while preserving corners and edges.
     * Perfect for pixel-art UI panels.
     * @param slicedSprite The sliced sprite data containing texture and slice dimensions
     * @param x Destination X
     * @param y Destination Y
     * @param width Destination width
     * @param height Destination height
     */
    default void drawSlicedSprite(SlicedSpriteData slicedSprite, int x, int y, int width, int height) {
        int u = slicedSprite.u();
        int v = slicedSprite.v();
        int texW = slicedSprite.texW();
        int texH = slicedSprite.texH();
        int left = slicedSprite.leftWidth();
        int right = slicedSprite.rightWidth();
        int top = slicedSprite.topHeight();
        int bottom = slicedSprite.bottomHeight();
        int atlasW = slicedSprite.atlasW();
        int atlasH = slicedSprite.atlasH();
        ResourceLocation texture = slicedSprite.texture();

        // Calculate center dimensions in source texture
        int centerW = texW - left - right;
        int centerH = texH - top - bottom;

        // Calculate stretched dimensions in destination
        int destCenterW = width - left - right;
        int destCenterH = height - top - bottom;

        // Don't render if the destination is too small for the edges
        if (destCenterW < 0 || destCenterH < 0) {
            return;
        }

        // Draw 9 slices:

        // Top-left corner
        drawTexture(texture, x, y, left, top, u, v, left, top, atlasW, atlasH);

        // Top edge (stretched horizontally)
        drawTexture(texture, x + left, y, destCenterW, top, u + left, v, centerW, top, atlasW, atlasH);

        // Top-right corner
        drawTexture(texture, x + left + destCenterW, y, right, top, u + left + centerW, v, right, top, atlasW, atlasH);

        // Left edge (stretched vertically)
        drawTexture(texture, x, y + top, left, destCenterH, u, v + top, left, centerH, atlasW, atlasH);

        // Center (stretched both ways)
        drawTexture(texture, x + left, y + top, destCenterW, destCenterH, u + left, v + top, centerW, centerH, atlasW, atlasH);

        // Right edge (stretched vertically)
        drawTexture(texture, x + left + destCenterW, y + top, right, destCenterH, u + left + centerW, v + top, right, centerH, atlasW, atlasH);

        // Bottom-left corner
        drawTexture(texture, x, y + top + destCenterH, left, bottom, u, v + top + centerH, left, bottom, atlasW, atlasH);

        // Bottom edge (stretched horizontally)
        drawTexture(texture, x + left, y + top + destCenterH, destCenterW, bottom, u + left, v + top + centerH, centerW, bottom, atlasW, atlasH);

        // Bottom-right corner
        drawTexture(texture, x + left + destCenterW, y + top + destCenterH, right, bottom, u + left + centerW, v + top + centerH, right, bottom, atlasW, atlasH);
    }

    /**
     * Draw a sprite with the specified render mode (STRETCH, REPEAT, or SLICE).
     * This is the primary method for rendering sprites with full control over behavior.
     * @param sprite The sprite data containing texture, UV coords, and render mode
     * @param x Destination X
     * @param y Destination Y
     * @param width Destination width
     * @param height Destination height
     */
    default void drawSprite(SpriteData sprite, int x, int y, int width, int height) {
        if (sprite == null || sprite.texture() == null) {
            return;
        }

        int u = sprite.u();
        int v = sprite.v();
        int regionW = sprite.regionW();
        int regionH = sprite.regionH();
        int textureW = sprite.textureW();
        int textureH = sprite.textureH();

        // If no dimensions specified, use simple texture draw
        if (regionW <= 0 || regionH <= 0) {
            drawTexture(sprite.texture(), x, y, width, height);
            return;
        }

        switch (sprite.renderMode()) {
            case REPEAT -> drawRepeatingTexture(sprite.texture(), x, y, width, height, u, v, regionW, regionH, textureW, textureH);
            case SLICE -> drawSlicedTexture(sprite.texture(), x, y, width, height, u, v, regionW, regionH,
                sprite.sliceLeft(), sprite.sliceRight(), sprite.sliceTop(), sprite.sliceBottom(), textureW, textureH, sprite.tileScale());
            case TILE -> drawTiledTexture(sprite.texture(), x, y, width, height, u, v, regionW, regionH,
                sprite.sliceLeft(), sprite.sliceRight(), sprite.sliceTop(), sprite.sliceBottom(), textureW, textureH, sprite.tileScale());
            default -> drawTexture(sprite.texture(), x, y, width, height, u, v, regionW, regionH, textureW, textureH);
        }
    }

    /**
     * Draw a repeating (tiling) texture.
     *
     * @param texture       ResourceLocation of the texture
     * @param x             Destination X
     * @param y             Destination Y
     * @param width         Destination width
     * @param height        Destination height
     * @param u             Source X in texture pixels
     * @param v             Source Y in texture pixels
     * @param regionWidth   Width of the source region in texture pixels
     * @param regionHeight  Height of the source region in texture pixels
     * @param textureWidth  Total width of the texture atlas
     * @param textureHeight Total height of the texture atlas
     */
    default void drawRepeatingTexture(ResourceLocation texture, int x, int y, int width, int height,
                                      int u, int v, int regionWidth, int regionHeight,
                                      int textureWidth, int textureHeight) {
        // Calculate how many times to tile horizontally and vertically
        int tilesX = (width + regionWidth - 1) / regionWidth; // ceiling division
        int tilesY = (height + regionHeight - 1) / regionHeight;

        for (int ty = 0; ty < tilesY; ty++) {
            for (int tx = 0; tx < tilesX; tx++) {
                int drawX = x + tx * regionWidth;
                int drawY = y + ty * regionHeight;

                // Calculate how much of this tile to draw (for edge tiles)
                int drawW = Math.min(regionWidth, width - tx * regionWidth);
                int drawH = Math.min(regionHeight, height - ty * regionHeight);

                // Draw the tile (potentially clipped at edges)
                drawTexture(texture, drawX, drawY, drawW, drawH, u, v, drawW, drawH, textureWidth, textureHeight);
            }
        }
    }

    /**
     * Draw a 9-slice texture.
     *
     * @param texture  ResourceLocation of the texture
     * @param x        Destination X
     * @param y        Destination Y
     * @param width    Destination width
     * @param height   Destination height
     * @param u        Source X in texture pixels
     * @param v        Source Y in texture pixels
     * @param regionW  Total width of the source region
     * @param regionH  Total height of the source region
     * @param left     Width of the left slice
     * @param right    Width of the right slice
     * @param top      Height of the top slice
     * @param bottom   Height of the bottom slice
     * @param textureW Total width of the texture atlas
     * @param textureH Total height of the texture atlas
     * @param tileScale Scale factor for tiles (1.0 = original size)
     */
    default void drawSlicedTexture(ResourceLocation texture, int x, int y, int width, int height,
                                   int u, int v, int regionW, int regionH,
                                   int left, int right, int top, int bottom,
                                   int textureW, int textureH, float tileScale) {
        // Calculate center dimensions in source texture
        int centerW = regionW - left - right;
        int centerH = regionH - top - bottom;

        // Apply tile scaling to slice dimensions
        int scaledLeft = Math.max(1, (int)(left * tileScale));
        int scaledRight = Math.max(1, (int)(right * tileScale));
        int scaledTop = Math.max(1, (int)(top * tileScale));
        int scaledBottom = Math.max(1, (int)(bottom * tileScale));

        // Calculate stretched dimensions in destination
        int destCenterW = width - scaledLeft - scaledRight;
        int destCenterH = height - scaledTop - scaledBottom;

        // Don't render if the destination is too small for the edges
        if (destCenterW < 0 || destCenterH < 0) {
            return;
        }

        // Draw 9 slices:

        // Top-left corner
        drawTexture(texture, x, y, scaledLeft, scaledTop, u, v, left, top, textureW, textureH);

        // Top edge (stretched horizontally)
        drawTexture(texture, x + scaledLeft, y, destCenterW, scaledTop, u + left, v, centerW, top, textureW, textureH);

        // Top-right corner
        drawTexture(texture, x + scaledLeft + destCenterW, y, scaledRight, scaledTop, u + left + centerW, v, right, top, textureW, textureH);

        // Left edge (stretched vertically)
        drawTexture(texture, x, y + scaledTop, scaledLeft, destCenterH, u, v + top, left, centerH, textureW, textureH);

        // Center (stretched both ways)
        drawTexture(texture, x + scaledLeft, y + scaledTop, destCenterW, destCenterH, u + left, v + top, centerW, centerH, textureW, textureH);

        // Right edge (stretched vertically)
        drawTexture(texture, x + scaledLeft + destCenterW, y + scaledTop, scaledRight, destCenterH, u + left + centerW, v + top, right, centerH, textureW, textureH);

        // Bottom-left corner
        drawTexture(texture, x, y + scaledTop + destCenterH, scaledLeft, scaledBottom, u, v + top + centerH, left, bottom, textureW, textureH);

        // Bottom edge (stretched horizontally)
        drawTexture(texture, x + scaledLeft, y + scaledTop + destCenterH, destCenterW, scaledBottom, u + left, v + top + centerH, centerW, bottom, textureW, textureH);

        // Bottom-right corner
        drawTexture(texture, x + scaledLeft + destCenterW, y + scaledTop + destCenterH, scaledRight, scaledBottom, u + left + centerW, v + top + centerH, right, bottom, textureW, textureH);
    }

    /**
     * Draw a 9-slice tiled texture with tessellation instead of stretching.
     *
     * @param texture  ResourceLocation of the texture
     * @param x        Destination X
     * @param y        Destination Y
     * @param width    Destination width
     * @param height   Destination height
     * @param u        Source X in texture pixels
     * @param v        Source Y in texture pixels
     * @param regionW  Total width of the source region
     * @param regionH  Total height of the source region
     * @param left     Width of the left slice
     * @param right    Width of the right slice
     * @param top      Height of the top slice
     * @param bottom   Height of the bottom slice
     * @param textureW Total width of the texture atlas
     * @param textureH Total height of the texture atlas
     * @param tileScale Scale factor for tiles (1.0 = original size, 2.0 = double size, 0.5 = half size)
     */
    default void drawTiledTexture(ResourceLocation texture, int x, int y, int width, int height,
                                  int u, int v, int regionW, int regionH,
                                  int left, int right, int top, int bottom,
                                  int textureW, int textureH, float tileScale) {
        // Calculate center dimensions in source texture
        int centerW = regionW - left - right;
        int centerH = regionH - top - bottom;

        // Calculate destination center area
        int destCenterW = width - left - right;
        int destCenterH = height - top - bottom;

        // Don't render if the destination is too small for the edges
        if (destCenterW < 0 || destCenterH < 0) {
            return;
        }

        // Apply tile scaling to the source tile sizes
        int scaledCenterW = Math.max(1, (int)(centerW * tileScale));
        int scaledCenterH = Math.max(1, (int)(centerH * tileScale));
        int scaledLeft = Math.max(1, (int)(left * tileScale));
        int scaledRight = Math.max(1, (int)(right * tileScale));
        int scaledTop = Math.max(1, (int)(top * tileScale));
        int scaledBottom = Math.max(1, (int)(bottom * tileScale));

        // Recalculate destination areas with scaled corners
        destCenterW = width - scaledLeft - scaledRight;
        destCenterH = height - scaledTop - scaledBottom;

        // Don't render if the destination is too small for the scaled edges
        if (destCenterW < 0 || destCenterH < 0) {
            return;
        }

        // Draw 9 slices:

        // Top-left corner (scaled)
        drawTexture(texture, x, y, scaledLeft, scaledTop, u, v, left, top, textureW, textureH);

        // Top edge (tessellate horizontally)
        drawTiledEdge(texture, x + scaledLeft, y, destCenterW, scaledTop, u + left, v, centerW, top, textureW, textureH, tileScale, true);

        // Top-right corner (scaled)
        drawTexture(texture, x + scaledLeft + destCenterW, y, scaledRight, scaledTop, u + left + centerW, v, right, top, textureW, textureH);

        // Left edge (tessellate vertically)
        drawTiledEdge(texture, x, y + scaledTop, scaledLeft, destCenterH, u, v + top, left, centerH, textureW, textureH, tileScale, false);

        // Center (tessellate in both directions)
        drawTiledCenter(texture, x + scaledLeft, y + scaledTop, destCenterW, destCenterH, u + left, v + top, centerW, centerH, textureW, textureH, tileScale);

        // Right edge (tessellate vertically)
        drawTiledEdge(texture, x + scaledLeft + destCenterW, y + scaledTop, scaledRight, destCenterH, u + left + centerW, v + top, right, centerH, textureW, textureH, tileScale, false);

        // Bottom-left corner (scaled)
        drawTexture(texture, x, y + scaledTop + destCenterH, scaledLeft, scaledBottom, u, v + top + centerH, left, bottom, textureW, textureH);

        // Bottom edge (tessellate horizontally)
        drawTiledEdge(texture, x + scaledLeft, y + scaledTop + destCenterH, destCenterW, scaledBottom, u + left, v + top + centerH, centerW, bottom, textureW, textureH, tileScale, true);

        // Bottom-right corner (scaled)
        drawTexture(texture, x + scaledLeft + destCenterW, y + scaledTop + destCenterH, scaledRight, scaledBottom, u + left + centerW, v + top + centerH, right, bottom, textureW, textureH);
    }

    /**
     * Draw a tiled edge (either horizontal or vertical tessellation).
     *
     * @param texture ResourceLocation of the texture
     * @param x Destination X
     * @param y Destination Y
     * @param width Destination width
     * @param height Destination height
     * @param u Source X in texture pixels
     * @param v Source Y in texture pixels
     * @param regionW Width of the source region in texture pixels
     * @param regionH Height of the source region in texture pixels
     * @param textureW Total width of the texture atlas
     * @param textureH Total height of the texture atlas
     * @param tileScale Scale factor for tiles
     * @param horizontal If true, tile horizontally; if false, tile vertically
     */
    default void drawTiledEdge(ResourceLocation texture, int x, int y, int width, int height,
                               int u, int v, int regionW, int regionH,
                               int textureW, int textureH, float tileScale, boolean horizontal) {
        if (horizontal) {
            // Tile horizontally
            int scaledTileW = Math.max(1, (int)(regionW * tileScale));
            int tilesX = (width + scaledTileW - 1) / scaledTileW; // ceiling division

            for (int tx = 0; tx < tilesX; tx++) {
                int drawX = x + tx * scaledTileW;
                int drawW = Math.min(scaledTileW, width - tx * scaledTileW);

                // Calculate proportional source region width to avoid squishing
                int srcW = (drawW == scaledTileW) ? regionW : (int)((drawW / (float)scaledTileW) * regionW);

                drawTexture(texture, drawX, y, drawW, height, u, v, srcW, regionH, textureW, textureH);
            }
        } else {
            // Tile vertically
            int scaledTileH = Math.max(1, (int)(regionH * tileScale));
            int tilesY = (height + scaledTileH - 1) / scaledTileH; // ceiling division

            for (int ty = 0; ty < tilesY; ty++) {
                int drawY = y + ty * scaledTileH;
                int drawH = Math.min(scaledTileH, height - ty * scaledTileH);

                // Calculate proportional source region height to avoid squishing
                int srcH = (drawH == scaledTileH) ? regionH : (int)((drawH / (float)scaledTileH) * regionH);

                drawTexture(texture, x, drawY, width, drawH, u, v, regionW, srcH, textureW, textureH);
            }
        }
    }

    /**
     * Draw a tiled center area (tessellation in both directions).
     *
     * @param texture ResourceLocation of the texture
     * @param x Destination X
     * @param y Destination Y
     * @param width Destination width
     * @param height Destination height
     * @param u Source X in texture pixels
     * @param v Source Y in texture pixels
     * @param regionW Width of the source region in texture pixels
     * @param regionH Height of the source region in texture pixels
     * @param textureW Total width of the texture atlas
     * @param textureH Total height of the texture atlas
     * @param tileScale Scale factor for tiles
     */
    default void drawTiledCenter(ResourceLocation texture, int x, int y, int width, int height,
                                 int u, int v, int regionW, int regionH,
                                 int textureW, int textureH, float tileScale) {
        int scaledTileW = Math.max(1, (int)(regionW * tileScale));
        int scaledTileH = Math.max(1, (int)(regionH * tileScale));

        int tilesX = (width + scaledTileW - 1) / scaledTileW; // ceiling division
        int tilesY = (height + scaledTileH - 1) / scaledTileH; // ceiling division

        for (int ty = 0; ty < tilesY; ty++) {
            for (int tx = 0; tx < tilesX; tx++) {
                int drawX = x + tx * scaledTileW;
                int drawY = y + ty * scaledTileH;

                // Calculate how much of this tile to draw (for edge tiles)
                int drawW = Math.min(scaledTileW, width - tx * scaledTileW);
                int drawH = Math.min(scaledTileH, height - ty * scaledTileH);

                // Calculate proportional source region sizes to avoid squishing
                int srcW = (drawW == scaledTileW) ? regionW : (int)((drawW / (float)scaledTileW) * regionW);
                int srcH = (drawH == scaledTileH) ? regionH : (int)((drawH / (float)scaledTileH) * regionH);

                drawTexture(texture, drawX, drawY, drawW, drawH, u, v, srcW, srcH, textureW, textureH);
            }
        }
    }
}
