package io.github.currenj.gelatinui.gui.components;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

/**
 * Data holder describing a sprite region within a texture atlas.
 * All fields are mutable to avoid costly record instantiation during rendering.
 * texture: the ResourceLocation for the texture atlas
 * u,v: source origin in texture pixels
 * regionW,regionH: size of the source region in texture pixels (may include padding)
 * actualW,actualH: actual content size inside the source region (for centered cropping); 0 means use full region
 * textureW,textureH: total size of the texture atlas (default 256x256 if not specified)
 * renderMode: how the sprite should be rendered (STRETCH, REPEAT, SLICE, TILE, or ITEM)
 * sliceLeft, sliceRight, sliceTop, sliceBottom: dimensions for 9-slice rendering (only used when renderMode is SLICE or TILE)
 * tileScale: scale factor for tiled sprites (default 1.0, can be 0.5 or 2.0 for nice repeating)
 * itemId: ResourceLocation of the item to render (only used when renderMode is ITEM)
 * itemRotation: rotation vector (X, Y, Z) in degrees for item rendering (default (0,0,0))
 * zOffset: Z-offset to apply to the rendered sprite (default 0)
 */
public class SpriteData {
    private ResourceLocation texture;
    private int u;
    private int v;
    private int regionW;
    private int regionH;
    private int actualW;
    private int actualH;
    private int textureW;
    private int textureH;
    private SpriteRenderMode renderMode;
    private int sliceLeft;
    private int sliceRight;
    private int sliceTop;
    private int sliceBottom;
    private float tileScale;
    private ResourceLocation itemId;
    private Vector3f itemRotation;
    private float zOffset;

    public SpriteData(ResourceLocation texture, int u, int v, int regionW, int regionH, int actualW, int actualH,
                      int textureW, int textureH, SpriteRenderMode renderMode, int sliceLeft, int sliceRight,
                      int sliceTop, int sliceBottom, float tileScale, ResourceLocation itemId, Vector3f itemRotation, float zOffset) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.regionW = regionW;
        this.regionH = regionH;
        this.actualW = actualW;
        this.actualH = actualH;
        this.textureW = textureW;
        this.textureH = textureH;
        this.renderMode = renderMode;
        this.sliceLeft = sliceLeft;
        this.sliceRight = sliceRight;
        this.sliceTop = sliceTop;
        this.sliceBottom = sliceBottom;
        this.tileScale = tileScale;
        this.itemId = itemId;
        this.itemRotation = itemRotation != null ? new Vector3f(itemRotation) : new Vector3f(0, 0, 0);
        this.zOffset = zOffset;
        validate();
    }
    public SpriteData(ResourceLocation texture) {
        this(texture, 0, 0, 0, 0, 0, 0, 256, 256, SpriteRenderMode.STRETCH, 0, 0, 0, 0, 1.0f, null, new Vector3f(0, 0, 0), 0);
    }

    public static SpriteData texture(ResourceLocation texture) {
        return new SpriteData(texture);
    }

    // Getters
    public ResourceLocation texture() { return texture; }
    public int u() { return u; }
    public int v() { return v; }
    public int regionW() { return regionW; }
    public int regionH() { return regionH; }
    public int actualW() { return actualW; }
    public int actualH() { return actualH; }
    public int textureW() { return textureW; }
    public int textureH() { return textureH; }
    public SpriteRenderMode renderMode() { return renderMode; }
    public int sliceLeft() { return sliceLeft; }
    public int sliceRight() { return sliceRight; }
    public int sliceTop() { return sliceTop; }
    public int sliceBottom() { return sliceBottom; }
    public float tileScale() { return tileScale; }
    public ResourceLocation itemId() { return itemId; }
    public Vector3f itemRotation() { return itemRotation; }
    public float zOffset() { return zOffset; }

    // Setters (fluent API - returns this for chaining)
    public SpriteData setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public SpriteData uv(int u) {
        this.u = u;
        return this;
    }

    public SpriteData uv(int u, int v) {
        this.u = u;
        this.v = v;
        return this;
    }

    public SpriteData uv(int u, int v, int regionW, int regionH) {
        this.u = u;
        this.v = v;
        this.regionW = regionW;
        this.regionH = regionH;
        return this;
    }

    public SpriteData renderMode(SpriteRenderMode renderMode) {
        this.renderMode = renderMode;
        validate();
        return this;
    }

    public SpriteData slice(int left, int right, int top, int bottom) {
        this.sliceLeft = left;
        this.sliceRight = right;
        this.sliceTop = top;
        this.sliceBottom = bottom;
        validate();
        return this;
    }

    public SpriteData textureSize(int textureSize) {
        this.textureW = textureSize;
        this.textureH = textureSize;
        return this;
    }

    public SpriteData textureSize(int textureSize, int textureHeight) {
        this.textureW = textureSize;
        this.textureH = textureHeight;
        return this;
    }

    public SpriteData actualSize(int actualW, int actualH) {
        this.actualW = actualW;
        this.actualH = actualH;
        return this;
    }

    public SpriteData tileScale(float tileScale) {
        this.tileScale = tileScale;
        return this;
    }

    /**
     * Set the item to render (for ITEM render mode).
     */
    public SpriteData itemId(ResourceLocation itemId) {
        this.itemId = itemId;
        validate();
        return this;
    }

    /**
     * Set item rotation angles (for ITEM render mode).
     */
    public SpriteData itemRotation(float rotationX, float rotationY, float rotationZ) {
        this.itemRotation.set(rotationX, rotationY, rotationZ);
        return this;
    }

    /**
     * Backwards-compatible 2-argument overload (rotationY, rotationZ) -> rotationX defaults to 0
     */
    public SpriteData itemRotation(float rotationY, float rotationZ) {
        return itemRotation(0.0f, rotationY, rotationZ);
    }

    /**
     * Set the z-offset for the sprite rendering.
     * This affects the rendering depth and can be used to control layering.
     */
    public SpriteData zOffset(float zOffset) {
        this.zOffset = zOffset;
        return this;
    }

    /**
     * Convenience method to create an ITEM mode sprite data.
     */
    public static SpriteData item(ResourceLocation itemId) {
        return new SpriteData(null, 0, 0, 0, 0, 0, 0, 256, 256, SpriteRenderMode.ITEM, 0, 0, 0, 0, 1.0f, itemId, new Vector3f(0, 0, 0), 0);
    }

    /**
     * Backwards-compatible 2-argument convenience (rotationY, rotationZ)
     */
    public static SpriteData item(ResourceLocation itemId, float rotationY, float rotationZ) {
        return item(itemId, 0.0f, rotationY, rotationZ);
    }

    /**
     * Convenience method to create an ITEM mode sprite data with rotation (rotationX, rotationY, rotationZ).
     */
    public static SpriteData item(ResourceLocation itemId, float rotationX, float rotationY, float rotationZ) {
        return new SpriteData(null, 0, 0, 0, 0, 0, 0, 256, 256, SpriteRenderMode.ITEM, 0, 0, 0, 0, 1.0f, itemId, new Vector3f(rotationX, rotationY, rotationZ), 0);
    }

    /**
     * Validate slice and item dimensions.
     */
    private void validate() {
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
