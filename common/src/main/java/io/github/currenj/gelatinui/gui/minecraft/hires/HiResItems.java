package io.github.currenj.gelatinui.gui.minecraft.hires;

import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;

/**
 * Entry point for drawing a GUI item at native screen resolution instead of blitting it magnified
 * out of the shared item atlas. See {@link HiResItemPipRenderer} for the why.
 */
public final class HiResItems {
    /**
     * Below this on-screen magnification the atlas blit is already pixel-for-pixel (or close
     * enough that a dedicated texture would only cost frame time), so the normal path is kept.
     */
    private static final float MIN_USEFUL_SCALE = 1.05f;

    /** A transform this degenerate (zero or near-zero scale) has nothing worth drawing. */
    private static final float MIN_SCALE = 1.0e-4f;

    /** Tolerance for treating a pose's off-diagonal terms as "axis aligned". */
    private static final float SKEW_EPSILON = 1.0e-4f;

    private HiResItems() {
    }

    /**
     * Draws {@code stack} filling the local-space square whose top-left corner is ({@code x},
     * {@code y}) and whose side is {@code size}, at the resolution it actually occupies on screen.
     *
     * <p>Only the off-screen texture is measured in whole GUI pixels; the item is then placed by
     * the ambient pose itself, so fractional positions (and rotations) land exactly where they
     * would have with an ordinary item draw, rather than snapping to the GUI pixel grid.
     *
     * <p>Returns {@code false} without drawing anything when the high-resolution path does not
     * apply — the item is not magnified, or the pose has collapsed it to nothing. Callers fall
     * back to {@code GuiGraphics#item} in that case.
     */
    public static boolean item(MinecraftRenderContext context, ItemStack stack, float x, float y, float size) {
        if (stack.isEmpty() || !HiResItemPipRenderer.isRegistered()) {
            return false;
        }

        GuiGraphicsExtractor graphics = context.getGraphics();
        Matrix3x2f pose = new Matrix3x2f(graphics.pose());

        // On-screen magnification along each axis — the length of each column of the pose's linear
        // part, so a rotated pose is measured the same as an upright one.
        float scaleX = (float) Math.sqrt(pose.m00 * pose.m00 + pose.m01 * pose.m01);
        float scaleY = (float) Math.sqrt(pose.m10 * pose.m10 + pose.m11 * pose.m11);
        if (scaleX < MIN_SCALE || scaleY < MIN_SCALE) {
            return false;
        }

        float width = size * scaleX;
        float height = size * scaleY;
        if (width / 16.0f < MIN_USEFUL_SCALE || height / 16.0f < MIN_USEFUL_SCALE) {
            return false;
        }

        // The texture is rasterised in whole GUI pixels (the picture-in-picture base class sizes it
        // as rectangle * guiScale), and the pose below stretches that rectangle back to the exact
        // requested size — at most half a pixel over its whole width, which no longer quantises
        // where the item sits.
        int textureWidth = Math.max(1, Math.round(width));
        int textureHeight = Math.max(1, Math.round(height));

        // Map the texture rectangle onto the square the caller asked for: the ambient pose, then
        // into the item's local box, then the texture's own pixel size stretched to fill it.
        Matrix3x2f blitPose = pose.translate(x, y).scale(size / textureWidth, size / textureHeight);
        snapToDevicePixels(blitPose);

        Minecraft minecraft = Minecraft.getInstance();
        TrackingItemStackRenderState item = new TrackingItemStackRenderState();
        minecraft.getItemModelResolver().updateForTopItem(item, stack, ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
        ((IGuiGraphicsExtension) graphics).gelatinui$submitPictureInPicture(
                new HiResItemRenderState(item, textureWidth, textureHeight, blitPose, context.peekScissor()));
        return true;
    }

    /**
     * Nudges an upright blit onto whole device pixels. The blit samples the texture with nearest
     * filtering, so a texture that lands half a device pixel off has one of its rows or columns
     * resolved inconsistently — a faint seam across an otherwise pixel-exact item. Rounding the
     * translation costs at most half a device pixel of position (finer than the screen can show
     * anyway) and is skipped entirely when the pose is rotated, where there is no grid to land on.
     */
    private static void snapToDevicePixels(Matrix3x2f pose) {
        if (Math.abs(pose.m01) > SKEW_EPSILON || Math.abs(pose.m10) > SKEW_EPSILON) {
            return;
        }
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        if (guiScale <= 0.0) {
            return;
        }
        pose.m20 = (float) (Math.round(pose.m20 * guiScale) / guiScale);
        pose.m21 = (float) (Math.round(pose.m21 * guiScale) / guiScale);
    }
}
