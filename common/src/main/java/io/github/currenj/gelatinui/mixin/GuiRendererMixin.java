package io.github.currenj.gelatinui.mixin;

import io.github.currenj.gelatinui.gui.minecraft.hires.HiResItemPipRenderer;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Per-frame tick for the high-resolution item renderer's texture pool, mirroring how vanilla
 * retires its unused oversized-item renderers once a frame.
 */
@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {

    @Inject(method = "endFrame", at = @At("TAIL"))
    private void gelatinui$endHiResItemFrame(CallbackInfo ci) {
        HiResItemPipRenderer.endFrame();
    }
}
