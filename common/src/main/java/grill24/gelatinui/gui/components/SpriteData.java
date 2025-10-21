package grill24.gelatinui.gui.components;

import net.minecraft.resources.ResourceLocation;

/**
 * Data holder describing a sprite region within a texture atlas.
 * texture: the ResourceLocation for the texture atlas
 * u,v: source origin in texture pixels
 * texW,texH: size of the source region in texture pixels (may include padding)
 * actualW,actualH: actual content size inside the source region (for centered cropping); 0 means use full region
 * atlasW,atlasH: total size of the texture atlas (default 256x256 if not specified)
 */
public record SpriteData(ResourceLocation texture, int u, int v, int texW, int texH, int actualW, int actualH, int atlasW, int atlasH) {
    public SpriteData(ResourceLocation texture) {
        this(texture, 0, 0, 0, 0, 0, 0, 256, 256);
    }

    public SpriteData(ResourceLocation texture, int u, int v, int texW, int texH) {
        this(texture, u, v, texW, texH, 0, 0, 256, 256);
    }

    public SpriteData(ResourceLocation texture, int u, int v, int texW, int texH, int actualW, int actualH) {
        this(texture, u, v, texW, texH, actualW, actualH, 256, 256);
    }
}
