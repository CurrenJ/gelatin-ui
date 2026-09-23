package io.github.currenj.gelatinui.mixin;

import io.github.currenj.gelatinui.gui.minecraft.hires.HiResItemPipRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers Gelatin's high-resolution item renderer with the GUI renderer's picture-in-picture
 * table. The vanilla list is built inline as an immutable {@code List.of(...)}, so it is replaced
 * here with a mutable copy that carries our renderer as well.
 *
 * <p>Fabric-only: NeoForge patches this same constructor call to take a list of
 * {@code PictureInPictureRendererRegistration} factories instead of renderer instances, built via
 * its own {@code RegisterPictureInPictureRenderersEvent}. Adding a raw renderer instance to that
 * list crashes with a {@code ClassCastException} when NeoForge tries to pool it, so the NeoForge
 * mixin config omits this mixin and registers through that event instead.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @org.spongepowered.asm.mixin.Final
    private RenderBuffers renderBuffers;

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;<init>(Lnet/minecraft/client/renderer/state/gui/GuiRenderState;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;Ljava/util/List;)V"),
            index = 4
    )
    private List<PictureInPictureRenderer<?>> gelatinui$addHiResItemRenderer(List<PictureInPictureRenderer<?>> renderers) {
        List<PictureInPictureRenderer<?>> withHiResItems = new ArrayList<>(renderers);
        withHiResItems.add(HiResItemPipRenderer.getOrCreate(this.renderBuffers.bufferSource()));
        return withHiResItems;
    }
}
