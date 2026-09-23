package io.github.currenj.gelatinui.gui.minecraft.hires;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

/**
 * A single item to be drawn at native screen resolution into a picture-in-picture texture, rather
 * than blitted from the shared GUI item atlas. See {@link HiResItemPipRenderer} for why.
 *
 * <p>The rectangle is only the texture's size in whole GUI pixels, anchored at the origin; where
 * that texture actually lands is carried by {@link #pose()}, which maps the rectangle onto the
 * exact (fractional, and possibly rotated) place the caller asked for. Keeping the two apart is
 * what lets the item sit anywhere on screen instead of snapping to the GUI pixel grid: only the
 * render resolution has to be whole pixels.
 */
public record HiResItemRenderState(
        TrackingItemStackRenderState item,
        int textureWidth,
        int textureHeight,
        Matrix3x2f pose,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {

    public HiResItemRenderState(TrackingItemStackRenderState item, int textureWidth, int textureHeight, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        this(item, textureWidth, textureHeight, pose, scissorArea, bounds(textureWidth, textureHeight, pose, scissorArea));
    }

    @Override
    public int x0() {
        return 0;
    }

    @Override
    public int y0() {
        return 0;
    }

    @Override
    public int x1() {
        return textureWidth;
    }

    @Override
    public int y1() {
        return textureHeight;
    }

    /**
     * Model units per rectangle: a GUI item model is centred on the origin and one unit wide, so
     * filling the destination rectangle means scaling by its size. (Vanilla's item atlas does the
     * same thing with its slot size; {@code OversizedItemRenderState} hardcodes 16 because its
     * rectangle is always in 16-pixel item units.)
     */
    @Override
    public float scale() {
        return Math.min(textureWidth, textureHeight);
    }

    @Nullable
    private static ScreenRectangle bounds(int textureWidth, int textureHeight, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle bounds = new ScreenRectangle(0, 0, textureWidth, textureHeight).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}
