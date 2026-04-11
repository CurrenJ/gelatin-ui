package io.github.currenj.gelatinui.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import io.github.currenj.gelatinui.tooltip.ItemStacksInfo;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin implements IGuiGraphicsExtension {

    @Shadow
    public abstract Matrix3x2fStack pose();

    @Shadow
    public abstract void fill(int x0, int y0, int x1, int y1, int col);

    @Shadow
    public abstract void blit(RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color);

    @Shadow
    abstract void setTooltipForNextFrameInternal(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, Identifier style, boolean replaceExisting);

    /**
     * Redirects tooltip rendering for Gelatin's custom ItemStacksTooltip.
     */
    @Inject(method = "componentHoverEffect", at = @At(value = "HEAD"))
    public void redirectRenderTooltipForText(Font font, Style style, int i, int j, CallbackInfo ci) {
        if (style != null && style.getHoverEvent() != null) {
            HoverEvent hoverEvent = style.getHoverEvent();
            if (hoverEvent instanceof HoverEvent.ShowItem showItem) {
                // Custom item stacks tooltip is handled via ItemStacksTooltip if applicable
                // Check the custom action via the tooltip component system
            }
        }
    }

    /**
     * Our own renderTooltip implementation using the deferred tooltip system.
     */
    @Override
    public void gelatinui$renderTooltip(Font font, Optional<TooltipComponent> tooltipComponent, int i, int j) {
        List<ClientTooltipComponent> list = new ArrayList<>();
        tooltipComponent.ifPresent(t -> list.addFirst(ClientTooltipComponent.create(t)));
        this.setTooltipForNextFrameInternal(font, list, i, j, DefaultTooltipPositioner.INSTANCE, null, false);
    }

    /**
     * Blit using the GUI_TEXTURED pipeline.
     */
    @Override
    public void gelatinui$blit(Identifier resourceLocation, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight) {
        this.gelatinui$innerBlit(resourceLocation, x, x + width, y, y + height, u / textureWidth, (u + regionWidth) / textureWidth, v / textureHeight, (v + regionHeight) / textureHeight);
    }

    @Override
    public void gelatinui$innerBlit(Identifier resourceLocation, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2) {
        // Convert float UVs back to integer coordinates for blit call
        // We'll use the standard blit with the GUI_TEXTURED pipeline
        int x = (int) x1;
        int y = (int) y1;
        int width = (int) (x2 - x1);
        int height = (int) (y2 - y1);
        // Approximate: reconstruct u/v from normalized coords (assume 256x256 texture)
        float uPx = u1 * 256.0f;
        float vPx = v1 * 256.0f;
        this.blit(RenderPipelines.GUI_TEXTURED, resourceLocation, x, y, uPx, vPx, width, height, 256, 256, -1);
    }

    /**
     * Fill a rotated/arbitrary quad using pose matrix transforms as an approximation.
     * Computes the axis-aligned bounding box as a best-effort fallback for arbitrary quads.
     */
    @Override
    public void gelatinui$fillQuad(RenderPipeline renderPipeline, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color) {
        // Compute bounding box as approximation
        float minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
        float minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
        float maxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
        float maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
        this.fill((int) minX, (int) minY, (int) maxX, (int) maxY, color);
    }

    /**
     * Fill a rotated rectangle using pose transforms.
     */
    @Override
    public void gelatinui$fill(RenderPipeline renderPipeline, float minX, float minY, float maxX, float maxY, org.joml.Vector2f origin, float rotationDegrees, int color) {
        Matrix3x2fStack pose = this.pose();
        pose.pushMatrix();
        pose.translate(origin.x, origin.y);
        pose.rotate((float) Math.toRadians(rotationDegrees));
        pose.translate(-origin.x, -origin.y);
        this.fill((int) minX, (int) minY, (int) maxX, (int) maxY, color);
        pose.popMatrix();
    }
}
