package io.github.currenj.gelatinui.gui.components;

import net.minecraft.resources.ResourceLocation;

/**
 * Data holder describing a sprite region within a texture atlas.
 * texture: the ResourceLocation for the texture atlas
 * u,v: source origin in texture pixels
 * regionW,regionH: size of the source region in texture pixels (may include padding)
 * actualW,actualH: actual content size inside the source region (for centered cropping); 0 means use full region
 * textureW,textureH: total size of the texture atlas (default 256x256 if not specified)
 * renderMode: how the sprite should be rendered (STRETCH, REPEAT, SLICE, TILE, or ITEM)
 * sliceLeft, sliceRight, sliceTop, sliceBottom: dimensions for 9-slice rendering (only used when renderMode is SLICE or TILE)
 * tileScale: scale factor for tiled sprites (default 1.0, can be 0.5 or 2.0 for nice repeating)
 * itemId: ResourceLocation of the item to render (only used when renderMode is ITEM)
 * itemRotationY: Y-axis rotation in degrees for item rendering (default 0)
 * itemRotationZ: Z-axis rotation in degrees for item rendering (default 0)
 * zOffset: Z-offset to apply to the rendered sprite (default 0)
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
    float tileScale,
    ResourceLocation itemId,
    float itemRotationY,
    float itemRotationZ,
    float zOffset
) {
    public SpriteData(ResourceLocation texture) {
        this(texture, 0, 0, 0, 0, 0, 0, 256, 256, SpriteRenderMode.STRETCH, 0, 0, 0, 0, 1.0f, null, 0, 0, 0);
    }

    public static SpriteData texture(ResourceLocation texture) {
        return new SpriteData(texture);
    }

    public SpriteData uv(int u)
    {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData uv(int u, int v) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData uv(int u, int v, int regionW, int regionH) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData renderMode(SpriteRenderMode renderMode) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData slice(int left, int right, int top, int bottom) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, left, right, top, bottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData textureSize(int textureSize) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureSize, textureSize, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData textureSize(int textureSize, int textureHeight)
    {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureSize, textureHeight, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData actualSize(int actualW, int actualH)
    {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    public SpriteData tileScale(float tileScale) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    /**
     * Set the item to render (for ITEM render mode).
     */
    public SpriteData itemId(ResourceLocation itemId) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    /**
     * Set item rotation angles (for ITEM render mode).
     */
    public SpriteData itemRotation(float rotationY, float rotationZ) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, rotationY, rotationZ, zOffset);
    }

    /**
     * Set the z-offset for the sprite rendering.
     * This affects the rendering depth and can be used to control layering.
     */
    public SpriteData zOffset(float zOffset) {
        return new SpriteData(texture, u, v, regionW, regionH, actualW, actualH, textureW, textureH, renderMode, sliceLeft, sliceRight, sliceTop, sliceBottom, tileScale, itemId, itemRotationY, itemRotationZ, zOffset);
    }

    /**
     * Convenience method to create an ITEM mode sprite data.
     */
    public static SpriteData item(ResourceLocation itemId) {
        return new SpriteData(null, 0, 0, 0, 0, 0, 0, 256, 256, SpriteRenderMode.ITEM, 0, 0, 0, 0, 1.0f, itemId, 0, 0, 0);
    }

    /**
     * Convenience method to create an ITEM mode sprite data with rotation.
     */
    public static SpriteData item(ResourceLocation itemId, float rotationY, float rotationZ) {
        return new SpriteData(null, 0, 0, 0, 0, 0, 0, 256, 256, SpriteRenderMode.ITEM, 0, 0, 0, 0, 1.0f, itemId, rotationY, rotationZ, 0);
    }

    /**
     * Validate slice and item dimensions.
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
        if (renderMode == SpriteRenderMode.ITEM && itemId == null) {
            throw new IllegalArgumentException("ITEM render mode requires itemId to be set");
        }
    }
}
