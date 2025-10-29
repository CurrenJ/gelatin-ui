package io.github.currenj.gelatinui.gui.components;

/**
 * Defines how a sprite should be rendered when the destination size differs from the source size.
 */
public enum SpriteRenderMode {
    /**
     * Stretch the entire sprite to fill the destination area.
     * This is the default mode for simple scaling.
     */
    STRETCH,

    /**
     * Repeat (tile) the sprite to fill the destination area.
     * Useful for texture patterns that should tile seamlessly.
     */
    REPEAT,

    /**
     * Use 9-slice scaling with preserved corners and edges.
     * Perfect for pixel-art UI panels that need to scale cleanly.
     * Requires slice dimensions to be set in SpriteData.
     */
    SLICE,

    /**
     * Use 9-slice scaling with preserved corners and tessellated edges/center.
     * Perfect for pixel-art UI panels that need to scale cleanly with repeating patterns.
     * Requires slice dimensions to be set in SpriteData.
     */
    TILE
}
