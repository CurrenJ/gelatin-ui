package io.github.currenj.gelatinui.gui.components;

import net.minecraft.resources.ResourceLocation;

/**
 * Data holder describing a sprite region within a texture atlas.
 * texture: the ResourceLocation for the texture atlas
 * u,v: source origin in texture pixels
 * regionW,regionH: size of the source region in texture pixels (may include padding)
 * actualW,actualH: actual content size inside the source region (for centered cropping); 0 means use full region
 * textureW,textureH: total size of the texture atlas (default 256x256 if not specified)
 * renderMode: how the sprite should be rendered (STRETCH, REPEAT, SLICE, or TILE)
 * sliceLeft, sliceRight, sliceTop, sliceBottom: dimensions for 9-slice rendering (only used when renderMode is SLICE or TILE)
 * tileScale: scale factor for tiled sprites (default 1.0, can be 0.5 or 2.0 for nice repeating)
 */
public record SpriteData(
    ResourceLocation texture,
    int u,
    int v,
    int regionW,
    int regionH,
    int actualW,
    int actualH,
    int textureW,
    int textureH,
    SpriteRenderMode renderMode,
    int sliceLeft,
    int sliceRight,
    int sliceTop,
    int sliceBottom,
    float tileScale
) {
    public SpriteData(ResourceLocation texture) {
        this(texture, 0, 0, 0, 0, 0, 0, 256, 256, SpriteRenderMode.STRETCH, 0, 0, 0, 0, 1.0f);
    }

    public static SpriteData texture(ResourceLocation texture) {
        return new SpriteData(texture);
    }

    public SpriteData uv(int u)
    {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    public SpriteData uv(int u, int v) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    public SpriteData uv(int u, int v, int regionW, int regionH) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    public SpriteData renderMode(SpriteRenderMode renderMode) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    public SpriteData slice(int left, int right, int top, int bottom) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, SpriteRenderMode.SLICE, left, right, top, bottom, tileScale);
    }

    public SpriteData textureSize(int textureSize) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureSize, textureSize, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    public SpriteData textureSize(int textureSize, int textureHeight)
    {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureSize, textureHeight, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    public SpriteData actualSize(int actualW, int actualH)
    {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    public SpriteData tileScale(float tileScale) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, SpriteRenderMode.TILE, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale);
    }

    /**
     * Validate slice dimensions.
     */
    public SpriteData {
        if (renderMode == SpriteRenderMode.SLICE || renderMode == SpriteRenderMode.TILE) {
            if (sliceLeft + sliceRight > regionW) {
                throw new IllegalArgumentException("Left + right slice (" + sliceLeft + " + " + sliceRight + ") exceeds texture width " + regionW);
            }
            if (sliceTop + sliceBottom > regionH) {
                throw new IllegalArgumentException("Top + bottom slice (" + sliceTop + " + " + sliceBottom + ") exceeds texture height " + regionH);
            }
        }
    }
}
