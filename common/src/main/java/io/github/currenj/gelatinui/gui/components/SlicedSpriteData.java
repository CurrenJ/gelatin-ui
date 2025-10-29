package io.github.currenj.gelatinui.gui.components;

import net.minecraft.resources.ResourceLocation;

/**
 * Data holder for a 9-slice sprite that can be scaled while preserving corner and edge details.
 * Perfect for pixel-art UI panels that need to scale cleanly.
 *
 * The sprite is divided into 9 regions:
 * <pre>
 * +-------+-------+-------+
 * |  TL   |  Top  |  TR   |
 * +-------+-------+-------+
 * | Left  | Center| Right |
 * +-------+-------+-------+
 * |  BL   | Bottom|  BR   |
 * +-------+-------+-------+
 * </pre>
 *
 * Corners are drawn at their original size.
 * Edges are stretched in one dimension.
 * Center is stretched in both dimensions.
 *
 * @param texture The ResourceLocation for the texture atlas
 * @param u Source X origin in texture pixels
 * @param v Source Y origin in texture pixels
 * @param texW Total width of the source region in texture pixels
 * @param texH Total height of the source region in texture pixels
 * @param leftWidth Width of the left edge/corner slices in pixels
 * @param rightWidth Width of the right edge/corner slices in pixels
 * @param topHeight Height of the top edge/corner slices in pixels
 * @param bottomHeight Height of the bottom edge/corner slices in pixels
 * @param atlasW Total width of the texture atlas (default 256)
 * @param atlasH Total height of the texture atlas (default 256)
 */
public record SlicedSpriteData(
    ResourceLocation texture,
    int u,
    int v,
    int texW,
    int texH,
    int leftWidth,
    int rightWidth,
    int topHeight,
    int bottomHeight,
    int atlasW,
    int atlasH
) {
    /**
     * Create a sliced sprite with uniform edge sizes.
     */
    public SlicedSpriteData(ResourceLocation texture, int u, int v, int texW, int texH, int edgeSize) {
        this(texture, u, v, texW, texH, edgeSize, edgeSize, edgeSize, edgeSize, 256, 256);
    }

    /**
     * Create a sliced sprite with separate horizontal and vertical edge sizes.
     */
    public SlicedSpriteData(ResourceLocation texture, int u, int v, int texW, int texH, int horizontalEdge, int verticalEdge) {
        this(texture, u, v, texW, texH, horizontalEdge, horizontalEdge, verticalEdge, verticalEdge, 256, 256);
    }

    /**
     * Create a sliced sprite with all edge sizes specified and default atlas size.
     */
    public SlicedSpriteData(ResourceLocation texture, int u, int v, int texW, int texH, int leftWidth, int rightWidth, int topHeight, int bottomHeight) {
        this(texture, u, v, texW, texH, leftWidth, rightWidth, topHeight, bottomHeight, 256, 256);
    }

    /**
     * Validate that the slice dimensions don't exceed the texture dimensions.
     */
    public SlicedSpriteData {
        if (leftWidth + rightWidth > texW) {
            throw new IllegalArgumentException("Left + right width (" + leftWidth + " + " + rightWidth + ") exceeds texture width " + texW);
        }
        if (topHeight + bottomHeight > texH) {
            throw new IllegalArgumentException("Top + bottom height (" + topHeight + " + " + bottomHeight + ") exceeds texture height " + texH);
        }
    }
}

