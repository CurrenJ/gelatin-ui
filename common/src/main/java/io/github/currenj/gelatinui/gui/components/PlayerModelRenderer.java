package io.github.currenj.gelatinui.gui.components;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.authlib.GameProfile;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A UI component that renders a posed player model wearing a real player's skin.
 * <p>
 * Draws the model the same way vanilla's skin-customization screen does
 * ({@code PlayerSkinWidget#renderWidget}), and resolves skins through the same pipeline a
 * player-head item uses (see {@link PlayerSkinLookup}), so skins resolve asynchronously (falling
 * back to the default Steve/Alex skin until the lookup lands) exactly like a player-head item does.
 * <p>
 * On 1.21.1 the model renders straight through the GUI pose stack, so like {@link ItemRenderer} it
 * draws in local element coordinates and inherits the ambient translate/scale.
 */
public class PlayerModelRenderer extends UIElement<PlayerModelRenderer> {
    private static final float MODEL_HEIGHT = 2.125f;
    private static final float FIT_SCALE = 0.97f;
    private static final float Z_OFFSET = 100.0f;
    private static final int FULL_BRIGHT = 15728880;

    private static PlayerModel<?> wideModel;
    private static PlayerModel<?> slimModel;

    private Supplier<PlayerSkin> skinLookup;
    private float rotationX = -5.0f;
    private float rotationY = 30.0f;
    private Consumer<PlayerModel<?>> poseApplier = model -> {};

    public PlayerModelRenderer(float width, float height) {
        this.size.set(width, height);
    }

    /**
     * Resolve and render the skin for {@code profile}. The profile must already carry a UUID (a
     * player who may not be online still works, e.g. {@code new GameProfile(uuid, null)}) — see
     * {@link PlayerSkinLookup} for why name-only resolution isn't supported on this version.
     */
    public PlayerModelRenderer profile(GameProfile profile) {
        this.skinLookup = PlayerSkinLookup.create(profile);
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
    public PlayerModelRenderer pose(Consumer<PlayerModel<?>> poseApplier) {
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

        PlayerSkin skin = skinLookup.get();
        PlayerModel<?> model = skin.model() == PlayerSkin.Model.SLIM ? slimModel : wideModel;
        resetPose(model);
        poseApplier.accept(model);
        syncOverlayParts(model);

        // Mirrors PlayerSkinWidget#renderWidget, with the model fitted and centered in this
        // element's box: feet on the bottom of a FIT_SCALE-tall band centered vertically.
        GuiGraphics graphics = mcContext.getGraphics();
        PoseStack pose = graphics.pose();
        float scale = FIT_SCALE * size.y / MODEL_HEIGHT;
        pose.pushPose();
        pose.translate(size.x / 2.0f, (size.y + scale * MODEL_HEIGHT) / 2.0f, Z_OFFSET);
        pose.scale(scale, scale, scale);
        pose.translate(0.0f, -0.0625f, 0.0f);
        pose.rotateAround(Axis.XP.rotationDegrees(rotationX), 0.0f, -1.0625f, 0.0f);
        pose.mulPose(Axis.YP.rotationDegrees(rotationY));
        graphics.flush();
        // 1.20.1's Lighting.setupForEntityInInventory takes no rotation argument (that overload
        // is a later addition); the fixed lighting setup still matches vanilla's own
        // PlayerSkinWidget-equivalent usage on this version.
        Lighting.setupForEntityInInventory();

        pose.pushPose();
        pose.scale(1.0f, 1.0f, -1.0f);
        pose.translate(0.0f, -1.5f, 0.0f);
        model.renderToBuffer(pose, graphics.bufferSource().getBuffer(model.renderType(skin.texture())), FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        pose.popPose();

        graphics.flush();
        Lighting.setupFor3DItems();
        pose.popPose();
    }

    private static void ensureModelsBaked() {
        if (wideModel != null) {
            return;
        }
        EntityModelSet models = Minecraft.getInstance().getEntityModels();
        wideModel = new PlayerModel<>(models.bakeLayer(ModelLayers.PLAYER), false);
        slimModel = new PlayerModel<>(models.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        wideModel.young = false;
        slimModel.young = false;
    }

    /** Zero every root part's rotation. */
    private static void resetPose(PlayerModel<?> model) {
        model.head.setRotation(0, 0, 0);
        model.body.setRotation(0, 0, 0);
        model.rightArm.setRotation(0, 0, 0);
        model.leftArm.setRotation(0, 0, 0);
        model.rightLeg.setRotation(0, 0, 0);
        model.leftLeg.setRotation(0, 0, 0);
    }

    /**
     * On 1.21.1 the overlay layers (hat/jacket/sleeves/pants) are siblings of the parts they cover,
     * not children, and {@code PlayerModel#setupAnim} copies the pose onto them. This renderer never
     * calls setupAnim, so copy them here.
     */
    private static void syncOverlayParts(PlayerModel<?> model) {
        model.hat.copyFrom(model.head);
        model.jacket.copyFrom(model.body);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftPants.copyFrom(model.leftLeg);
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
