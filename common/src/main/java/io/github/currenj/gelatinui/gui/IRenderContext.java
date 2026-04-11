package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.components.SlicedSpriteData;
import io.github.currenj.gelatinui.gui.components.SpriteData;
import net.minecraft.resources.Identifier;

/**
 * Abstraction layer for rendering operations.
 * Allows the GUI system to be independent of specific rendering implementations.
 */
public interface IRenderContext {
    void fill(int x1, int y1, int x2, int y2, int color);

    void drawString(String text, int x, int y, int color);

    void drawCenteredString(String text, int x, int y, int color);

    int getStringWidth(String text);

    int getFontHeight();

    void pushScissor(int x, int y, int width, int height);

    void popScissor();

    void enableBlend();

    void disableBlend();

    default void drawTexture(Identifier texture, float x, float y, int width, int height) {
        drawTexture(texture, x, y, width, height, 0, 0, width, height, 256, 256);
    }

    void drawTexture(Identifier texture, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight);

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
        Identifier texture = slicedSprite.texture();

        int centerW = texW - left - right;
        int centerH = texH - top - bottom;
        int destCenterW = width - left - right;
        int destCenterH = height - top - bottom;

        if (destCenterW < 0 || destCenterH < 0) return;

        drawTexture(texture, x, y, left, top, u, v, left, top, atlasW, atlasH);
        drawTexture(texture, x + left, y, destCenterW, top, u + left, v, centerW, top, atlasW, atlasH);
        drawTexture(texture, x + left + destCenterW, y, right, top, u + left + centerW, v, right, top, atlasW, atlasH);
        drawTexture(texture, x, y + top, left, destCenterH, u, v + top, left, centerH, atlasW, atlasH);
        drawTexture(texture, x + left, y + top, destCenterW, destCenterH, u + left, v + top, centerW, centerH, atlasW, atlasH);
        drawTexture(texture, x + left + destCenterW, y + top, right, destCenterH, u + left + centerW, v + top, right, centerH, atlasW, atlasH);
        drawTexture(texture, x, y + top + destCenterH, left, bottom, u, v + top + centerH, left, bottom, atlasW, atlasH);
        drawTexture(texture, x + left, y + top + destCenterH, destCenterW, bottom, u + left, v + top + centerH, centerW, bottom, atlasW, atlasH);
        drawTexture(texture, x + left + destCenterW, y + top + destCenterH, right, bottom, u + left + centerW, v + top + centerH, right, bottom, atlasW, atlasH);
    }

    default void drawSprite(SpriteData sprite, float x, float y, int width, int height) {
        if (sprite == null || sprite.texture() == null) return;

        int u = sprite.u();
        int v = sprite.v();
        int regionW = sprite.regionW();
        int regionH = sprite.regionH();
        int textureW = sprite.textureW();
        int textureH = sprite.textureH();

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

    default void drawRepeatingTexture(Identifier texture, float x, float y, int width, int height,
                                      int u, int v, int regionWidth, int regionHeight,
                                      int textureWidth, int textureHeight) {
        int tilesX = (width + regionWidth - 1) / regionWidth;
        int tilesY = (height + regionHeight - 1) / regionHeight;

        for (int ty = 0; ty < tilesY; ty++) {
            for (int tx = 0; tx < tilesX; tx++) {
                float drawX = x + tx * regionWidth;
                float drawY = y + ty * regionHeight;
                int drawW = Math.min(regionWidth, width - tx * regionWidth);
                int drawH = Math.min(regionHeight, height - ty * regionHeight);
                drawTexture(texture, drawX, drawY, drawW, drawH, u, v, drawW, drawH, textureWidth, textureHeight);
            }
        }
    }

    default void drawSlicedTexture(Identifier texture, float x, float y, int width, int height,
                                   int u, int v, int regionW, int regionH,
                                   int left, int right, int top, int bottom,
                                   int textureW, int textureH, float tileScale) {
        int centerW = regionW - left - right;
        int centerH = regionH - top - bottom;
        int scaledLeft = Math.max(1, (int)(left * tileScale));
        int scaledRight = Math.max(1, (int)(right * tileScale));
        int scaledTop = Math.max(1, (int)(top * tileScale));
        int scaledBottom = Math.max(1, (int)(bottom * tileScale));
        int destCenterW = width - scaledLeft - scaledRight;
        int destCenterH = height - scaledTop - scaledBottom;

        if (destCenterW < 0 || destCenterH < 0) return;

        drawTexture(texture, x, y, scaledLeft, scaledTop, u, v, left, top, textureW, textureH);
        drawTexture(texture, x + scaledLeft, y, destCenterW, scaledTop, u + left, v, centerW, top, textureW, textureH);
        drawTexture(texture, x + scaledLeft + destCenterW, y, scaledRight, scaledTop, u + left + centerW, v, right, top, textureW, textureH);
        drawTexture(texture, x, y + scaledTop, scaledLeft, destCenterH, u, v + top, left, centerH, textureW, textureH);
        drawTexture(texture, x + scaledLeft, y + scaledTop, destCenterW, destCenterH, u + left, v + top, centerW, centerH, textureW, textureH);
        drawTexture(texture, x + scaledLeft + destCenterW, y + scaledTop, scaledRight, destCenterH, u + left + centerW, v + top, right, centerH, textureW, textureH);
        drawTexture(texture, x, y + scaledTop + destCenterH, scaledLeft, scaledBottom, u, v + top + centerH, left, bottom, textureW, textureH);
        drawTexture(texture, x + scaledLeft, y + scaledTop + destCenterH, destCenterW, scaledBottom, u + left, v + top + centerH, centerW, bottom, textureW, textureH);
        drawTexture(texture, x + scaledLeft + destCenterW, y + scaledTop + destCenterH, scaledRight, scaledBottom, u + left + centerW, v + top + centerH, right, bottom, textureW, textureH);
    }

    default void drawTiledTexture(Identifier texture, float x, float y, int width, int height,
                                  int u, int v, int regionW, int regionH,
                                  int left, int right, int top, int bottom,
                                  int textureW, int textureH, float tileScale) {
        float centerW = regionW - left - right;
        float centerH = regionH - top - bottom;
        float scaledLeft = Math.max(1, (left * tileScale));
        float scaledRight = Math.max(1, (right * tileScale));
        float scaledTop = Math.max(1, (top * tileScale));
        float scaledBottom = Math.max(1, (bottom * tileScale));
        float destCenterW = width - scaledLeft - scaledRight;
        float destCenterH = height - scaledTop - scaledBottom;

        if (destCenterW < 0 || destCenterH < 0) return;

        drawTexture(texture, x, y, scaledLeft, scaledTop, u, v, left, top, textureW, textureH);
        drawTiledEdge(texture, x + scaledLeft, y, destCenterW, scaledTop, u + left, v, centerW, top, textureW, textureH, tileScale, true);
        drawTexture(texture, x + scaledLeft + destCenterW, y, scaledRight, scaledTop, u + left + centerW, v, right, top, textureW, textureH);
        drawTiledEdge(texture, x, y + scaledTop, scaledLeft, destCenterH, u, v + top, left, centerH, textureW, textureH, tileScale, false);
        drawTiledCenter(texture, x + scaledLeft, y + scaledTop, destCenterW, destCenterH, u + left, v + top, centerW, centerH, textureW, textureH, tileScale);
        drawTiledEdge(texture, x + scaledLeft + destCenterW, y + scaledTop, scaledRight, destCenterH, u + left + centerW, v + top, right, centerH, textureW, textureH, tileScale, false);
        drawTexture(texture, x, y + scaledTop + destCenterH, scaledLeft, scaledBottom, u, v + top + centerH, left, bottom, textureW, textureH);
        drawTiledEdge(texture, x + scaledLeft, y + scaledTop + destCenterH, destCenterW, scaledBottom, u + left, v + top + centerH, centerW, bottom, textureW, textureH, tileScale, true);
        drawTexture(texture, x + scaledLeft + destCenterW, y + scaledTop + destCenterH, scaledRight, scaledBottom, u + left + centerW, v + top + centerH, right, bottom, textureW, textureH);
    }

    default void drawTiledEdge(Identifier texture, float x, float y, float width, float height,
                               float u, float v, float regionW, float regionH,
                               int textureW, int textureH, float tileScale, boolean horizontal) {
        if (horizontal) {
            int scaledTileW = Math.max(1, (int)(regionW * tileScale));
            float tilesX = (width + scaledTileW - 1) / scaledTileW;
            for (int tx = 0; tx < tilesX; tx++) {
                float drawX = x + tx * scaledTileW;
                float drawW = Math.min(scaledTileW, width - tx * scaledTileW);
                float srcW = (drawW == scaledTileW) ? regionW : ((drawW / (float)scaledTileW) * regionW);
                drawTexture(texture, drawX, y, drawW, height, u, v, srcW, regionH, textureW, textureH);
            }
        } else {
            int scaledTileH = Math.max(1, (int)(regionH * tileScale));
            float tilesY = (height + scaledTileH - 1) / scaledTileH;
            for (int ty = 0; ty < tilesY; ty++) {
                float drawY = y + ty * scaledTileH;
                float drawH = Math.min(scaledTileH, height - ty * scaledTileH);
                float srcH = (drawH == scaledTileH) ? regionH : ((drawH / (float)scaledTileH) * regionH);
                drawTexture(texture, x, drawY, width, drawH, u, v, regionW, srcH, textureW, textureH);
            }
        }
    }

    default void drawTiledCenter(Identifier texture, float x, float y, float width, float height,
                                 float u, float v, float regionW, float regionH,
                                 int textureW, int textureH, float tileScale) {
        int scaledTileW = Math.max(1, (int)(regionW * tileScale));
        int scaledTileH = Math.max(1, (int)(regionH * tileScale));
        float tilesX = (width + scaledTileW - 1) / scaledTileW;
        float tilesY = (height + scaledTileH - 1) / scaledTileH;

        for (int ty = 0; ty < tilesY; ty++) {
            for (int tx = 0; tx < tilesX; tx++) {
                float drawX = x + tx * scaledTileW;
                float drawY = y + ty * scaledTileH;
                float drawW = Math.min(scaledTileW, width - tx * scaledTileW);
                float drawH = Math.min(scaledTileH, height - ty * scaledTileH);
                float srcW = (drawW == scaledTileW) ? regionW : ((drawW / (float)scaledTileW) * regionW);
                float srcH = (drawH == scaledTileH) ? regionH : ((drawH / (float)scaledTileH) * regionH);
                drawTexture(texture, drawX, drawY, drawW, drawH, u, v, srcW, srcH, textureW, textureH);
            }
        }
    }
}
