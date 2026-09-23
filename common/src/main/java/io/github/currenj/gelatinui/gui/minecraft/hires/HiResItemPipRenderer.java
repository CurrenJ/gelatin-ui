package io.github.currenj.gelatinui.gui.minecraft.hires;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Draws GUI items at native screen resolution.
 *
 * <p>Vanilla's {@code GuiGraphics#item} does not rasterise the item where it lands: it renders the
 * model once into the shared GUI item atlas at a fixed {@code 16 * guiScale} pixels, then blits
 * that cached square with {@code FilterMode.NEAREST} through the ambient GUI pose. An item drawn
 * with a pose scale above 1 is therefore a magnified low-resolution image — chunky texels and
 * staircased model silhouettes — no matter how large it appears on screen.
 *
 * <p>This renderer takes the same route vanilla reserves for oversized items and player skins: a
 * picture-in-picture texture sized to the item's actual on-screen rectangle, so the model is
 * rasterised once at exactly the resolution it will be shown at and blitted 1:1.
 *
 * <p>One {@link PictureInPictureRenderer} owns a single texture, so all states of a class drawn in
 * the same frame would otherwise overwrite each other (every picture-in-picture render happens
 * during prepare, while the blits are executed later). This class is therefore only a dispatcher:
 * it keeps one delegate renderer — one texture — per (model, size) pair drawn, exactly as vanilla
 * keeps a map of {@code OversizedItemRenderer}s keyed by model identity.
 */
public class HiResItemPipRenderer extends PictureInPictureRenderer<HiResItemRenderState> {
    /** Frames a delegate may go unused before its texture is released. */
    private static final int UNUSED_FRAMES_BEFORE_EVICTION = 200;

    /**
     * Ceiling on live textures. One is kept per (model, size) pair, and an item whose size is
     * animating produces a new size every frame, so the pool is bounded and the least recently
     * drawn entry is retired once it is full.
     */
    private static final int MAX_DELEGATES = 64;

    @Nullable
    private static HiResItemPipRenderer instance;

    private final MultiBufferSource.BufferSource bufferSource;
    private final Map<Key, Delegate> delegates = new HashMap<>();
    private long frame;

    public HiResItemPipRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        this.bufferSource = bufferSource;
        instance = this;
    }

    /**
     * Whether the renderer reached the GUI renderer's picture-in-picture table. A state submitted
     * without a registered renderer is silently dropped by vanilla, so callers check this first
     * and fall back to the ordinary item path rather than drawing nothing.
     */
    public static boolean isRegistered() {
        return instance != null;
    }

    /** Called once per GUI frame (see {@code GuiRendererMixin}) to retire unused textures. */
    public static void endFrame() {
        if (instance != null) {
            instance.evictUnused();
        }
    }

    @Override
    public Class<HiResItemRenderState> getRenderStateClass() {
        return HiResItemRenderState.class;
    }

    @Override
    public void prepare(HiResItemRenderState renderState, GuiRenderState guiRenderState, int guiScale) {
        Key key = new Key(renderState.item().getModelIdentity(), renderState.textureWidth(), renderState.textureHeight());
        Delegate delegate = delegates.get(key);
        if (delegate == null) {
            evictOldestIfFull();
            delegate = new Delegate(bufferSource);
            delegates.put(key, delegate);
        }
        delegate.lastUsedFrame = frame;
        delegate.prepare(renderState, guiRenderState, guiScale);
    }

    @Override
    protected void renderToTexture(HiResItemRenderState renderState, PoseStack poseStack) {
        drawItem(renderState.item(), poseStack);
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "gelatinui_hires_item";
    }

    @Override
    public void close() {
        delegates.values().forEach(PictureInPictureRenderer::close);
        delegates.clear();
        if (instance == this) {
            instance = null;
        }
        super.close();
    }

    /**
     * Retires the least recently drawn delegate when the pool is full. Never touches one drawn on
     * the frame being built, whose texture the queued blits still have to sample.
     */
    private void evictOldestIfFull() {
        while (delegates.size() >= MAX_DELEGATES) {
            Map.Entry<Key, Delegate> oldest = null;
            for (Map.Entry<Key, Delegate> entry : delegates.entrySet()) {
                if (oldest == null || entry.getValue().lastUsedFrame < oldest.getValue().lastUsedFrame) {
                    oldest = entry;
                }
            }
            if (oldest == null || oldest.getValue().lastUsedFrame == frame) {
                return;
            }
            oldest.getValue().close();
            delegates.remove(oldest.getKey());
        }
    }

    private void evictUnused() {
        frame++;
        Iterator<Delegate> it = delegates.values().iterator();
        while (it.hasNext()) {
            Delegate delegate = it.next();
            if (frame - delegate.lastUsedFrame > UNUSED_FRAMES_BEFORE_EVICTION) {
                delegate.close();
                it.remove();
            }
        }
    }

    /**
     * Submits the item model the same way {@code GuiItemAtlas#drawToSlot} does — the leading
     * {@code scale(1, -1, -1)} flips the model into GUI orientation on top of the scale the
     * picture-in-picture base class already applied.
     */
    private static void drawItem(TrackingItemStackRenderState item, PoseStack poseStack) {
        poseStack.scale(1.0F, -1.0F, -1.0F);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.gameRenderer.getLighting().setupFor(item.usesBlockLight() ? Lighting.Entry.ITEMS_3D : Lighting.Entry.ITEMS_FLAT);
        FeatureRenderDispatcher featureRenderDispatcher = minecraft.gameRenderer.getFeatureRenderDispatcher();
        SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();
        item.submit(poseStack, submitNodeStorage, 15728880, OverlayTexture.NO_OVERLAY, 0);
        featureRenderDispatcher.renderAllFeatures();
    }

    /** One texture's worth of item: a given model at a given on-screen size. */
    private static class Delegate extends PictureInPictureRenderer<HiResItemRenderState> {
        private long lastUsedFrame;
        @Nullable
        private Object modelOnTexture;

        private Delegate(MultiBufferSource.BufferSource bufferSource) {
            super(bufferSource);
        }

        @Override
        public Class<HiResItemRenderState> getRenderStateClass() {
            return HiResItemRenderState.class;
        }

        @Override
        protected void renderToTexture(HiResItemRenderState renderState, PoseStack poseStack) {
            drawItem(renderState.item(), poseStack);
            modelOnTexture = renderState.item().getModelIdentity();
        }

        @Override
        protected boolean textureIsReadyToBlit(HiResItemRenderState renderState) {
            TrackingItemStackRenderState item = renderState.item();
            return !item.isAnimated() && item.getModelIdentity().equals(modelOnTexture);
        }

        @Override
        protected float getTranslateY(int height, int guiScale) {
            return height / 2.0F;
        }

        @Override
        protected String getTextureLabel() {
            return "gelatinui_hires_item";
        }
    }

    private record Key(Object modelIdentity, int width, int height) {
    }
}
