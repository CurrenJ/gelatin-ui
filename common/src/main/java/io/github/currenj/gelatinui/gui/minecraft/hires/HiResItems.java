package io.github.currenj.gelatinui.gui.minecraft.hires;

import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.world.item.ItemStack;

/**
 * Entry point for drawing a GUI item at native screen resolution instead of blitting it magnified
 * out of the shared item atlas.
 *
 * <p>On 1.21.1 this is a no-op that always declines. The problem it solves on 26.1 doesn't exist
 * here: 1.21.1's {@code GuiGraphics#renderItem} draws the item model through the pose, so a
 * magnified item is already rasterised at the size it occupies on screen. The method is kept so
 * callers compile unchanged across versions.
 */
public final class HiResItems {
    private HiResItems() {
    }

    /**
     * Would draw {@code stack} filling the local-space square whose top-left corner is ({@code x},
     * {@code y}) and whose side is {@code size}, at the resolution it actually occupies on screen.
     *
     * <p>Always returns {@code false} without drawing anything on 1.21.1; callers fall back to
     * {@code GuiGraphics#renderItem}, which is already sharp at any scale.
     */
    public static boolean item(MinecraftRenderContext context, ItemStack stack, float x, float y, float size) {
        return false;
    }
}
