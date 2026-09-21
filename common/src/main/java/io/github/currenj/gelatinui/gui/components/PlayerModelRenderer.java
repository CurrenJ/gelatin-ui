package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Vector2f;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A UI component that renders a posed player model wearing a real player's skin.
 * <p>
 * Built on the same {@code GuiGraphicsExtractor#skin(...)} "picture in picture" primitive vanilla
 * uses for the skin-customization screen's {@code PlayerSkinWidget}, and the same
 * {@link PlayerSkinRenderCache} + {@link ResolvableProfile} skin-resolution pipeline vanilla uses
 * for player-head item stacks — so skins resolve asynchronously (falling back to the default
 * Steve/Alex skin until the lookup lands) exactly like a player-head item does.
 * <p>
 * Unlike {@link ItemRenderer}, the underlying render call is not pose-stack aware: it takes an
 * absolute screen-pixel rectangle rather than local element coordinates. {@link #renderSelf}
 * accounts for this by reading this element's global (screen-space) position/scale rather than
 * emitting local (0,0) coordinates the way other leaf elements do.
 */
public class PlayerModelRenderer extends UIElement<PlayerModelRenderer> {
    private static final float MODEL_HEIGHT = 2.125f;
    private static final float FIT_SCALE = 0.97f;

    private static PlayerModel wideModel;
    private static PlayerModel slimModel;

    private Supplier<PlayerSkinRenderCache.RenderInfo> skinLookup;
    private float rotationX = -5.0f;
    private float rotationY = 30.0f;
    private Consumer<PlayerModel> poseApplier = model -> {};

    public PlayerModelRenderer(float width, float height) {
        this.size.set(width, height);
    }

    /**
     * Resolve and render the skin for {@code profile}. Use
     * {@link ResolvableProfile#createUnresolved(java.util.UUID)} /
     * {@link ResolvableProfile#createUnresolved(String)} for a player who may not be online —
     * resolution happens the same way a player-head item resolves its texture.
     */
    public PlayerModelRenderer profile(ResolvableProfile profile) {
        this.skinLookup = Minecraft.getInstance().playerSkinRenderCache().createLookup(profile);
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /** Whole-model view angles (degrees), not a per-limb pose. Vanilla's skin widget default is (-5, 30). */
    public PlayerModelRenderer rotation(float rotationX, float rotationY) {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /**
     * Set per-limb pose. Called every frame before render with a model whose parts have already
     * been reset to the neutral standing pose (all rotations zeroed) — only set the parts you want
     * to pose away from neutral. See {@link PlayerPoses} for ready-made presets.
     */
    public PlayerModelRenderer pose(Consumer<PlayerModel> poseApplier) {
        this.poseApplier = poseApplier != null ? poseApplier : model -> {};
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Skin resolution and pose are resolved fresh each render call; nothing to tick here.
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (skinLookup == null || !(context instanceof MinecraftRenderContext mcContext)) {
            return;
        }

        ensureModelsBaked();

        PlayerSkin skin = skinLookup.get().playerSkin();
        PlayerModel model = skin.model() == PlayerModelType.SLIM ? slimModel : wideModel;
        resetPose(model);
        poseApplier.accept(model);

        // graphics.skin(...) is a picture-in-picture render call: it takes an absolute
        // screen-pixel rectangle, not local coordinates transformed by the ambient pose stack
        // (unlike graphics.item(...), which ItemRenderer relies on). Compute the global rect
        // ourselves rather than emitting (0,0)-(size.x,size.y) like other leaf elements do.
        Vector2f globalPos = getGlobalPosition();
        float globalScale = getGlobalScale();
        int x0 = Math.round(globalPos.x);
        int y0 = Math.round(globalPos.y);
        int x1 = Math.round(globalPos.x + size.x * globalScale);
        int y1 = Math.round(globalPos.y + size.y * globalScale);

        float scale = FIT_SCALE * (size.y * globalScale) / MODEL_HEIGHT;

        mcContext.getGraphics().skin(model, skin.body().texturePath(), scale, rotationX, rotationY, -1.0625f, x0, y0, x1, y1);
    }

    private static void ensureModelsBaked() {
        if (wideModel != null) {
            return;
        }
        EntityModelSet models = Minecraft.getInstance().getEntityModels();
        wideModel = new PlayerModel(models.bakeLayer(ModelLayers.PLAYER), false);
        slimModel = new PlayerModel(models.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    /**
     * Zero every root part's rotation. Overlay layers (hat/jacket/sleeves/pants) are children of
     * these parts in {@link PlayerModel} and follow automatically — no separate reset needed.
     */
    private static void resetPose(PlayerModel model) {
        model.head.setRotation(0, 0, 0);
        model.body.setRotation(0, 0, 0);
        model.rightArm.setRotation(0, 0, 0);
        model.leftArm.setRotation(0, 0, 0);
        model.rightLeg.setRotation(0, 0, 0);
        model.leftLeg.setRotation(0, 0, 0);
    }

    @Override
    protected String getDefaultDebugName() {
        return "PlayerModelRenderer";
    }

    @Override
    protected PlayerModelRenderer self() {
        return this;
    }
}
