package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.Supplier;

/**
 * A UI component that renders a fully-equipped player (skin, armor, held item) in a GUI, using
 * vanilla's own equipped-entity render pipeline rather than reimplementing item/armor layering.
 * <p>
 * Built on the same picture-in-picture mechanism vanilla uses to preview the local player in the
 * inventory/character screen ({@code InventoryScreen#extractEntityInInventoryFollowsMouse}): a
 * real {@link net.minecraft.world.entity.LivingEntity} is snapshotted into an
 * {@link EntityRenderState} via {@code EntityRenderDispatcher#getRenderer(entity)
 * #createRenderState(entity, partialTick)}, then that state is fed to
 * {@code GuiGraphicsExtractor#entity(...)}. Unlike {@link PlayerModelRenderer} (which draws a bare
 * skin via {@code GuiGraphicsExtractor#skin(...)}), this goes through vanilla's full
 * {@code LivingEntityRenderer} layer stack, so armor and held items render for free.
 * <p>
 * The entity behind this is a {@link ClientMannequin} — MC's own puppet type for displaying a
 * posed player skin — constructed purely as an off-screen data holder: it is never added to a
 * level ({@code Level#addFreshEntity} is never called) and never ticked, so it never participates
 * in AI, physics, or collision. Skin resolution deliberately bypasses {@code ClientMannequin}'s own
 * profile-driven lookup (which goes through private setters not reachable from mod code) in favor
 * of the same {@link PlayerSkinRenderCache} lookup {@link PlayerModelRenderer} already uses; the
 * resolved skin is written directly onto the extracted {@link AvatarRenderState} after creation,
 * the same way vanilla's {@code InventoryScreen} overwrites {@code bodyRot}/{@code xRot}/
 * {@code yRot} on an extracted render state rather than moving the real entity.
 */
public class PlayerAvatarRenderer extends UIElement<PlayerAvatarRenderer> {
    private static final float MODEL_HEIGHT = 2.125f;
    private static final float FIT_SCALE = 0.97f;

    private Supplier<PlayerSkinRenderCache.RenderInfo> skinLookup;
    private ItemStack heldItem = ItemStack.EMPTY;
    // 180 is vanilla's own "facing the camera" baseline for a GUI entity preview — see
    // InventoryScreen#extractEntityInInventoryFollowsMouse, which uses the same convention.
    private float bodyYRot = 180.0f;
    private float xRot = 0.0f;

    private ClientMannequin puppet;

    public PlayerAvatarRenderer(float width, float height) {
        this.size.set(width, height);
    }

    /**
     * Resolve and render the skin for {@code profile}. Use
     * {@link ResolvableProfile#createUnresolved(java.util.UUID)} /
     * {@link ResolvableProfile#createUnresolved(String)} for a player who may not be online.
     */
    public PlayerAvatarRenderer profile(ResolvableProfile profile) {
        this.skinLookup = Minecraft.getInstance().playerSkinRenderCache().createLookup(profile);
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /** Item to show in the player's main hand. {@link ItemStack#EMPTY} for empty-handed. */
    public PlayerAvatarRenderer heldItem(ItemStack heldItem) {
        this.heldItem = heldItem != null ? heldItem : ItemStack.EMPTY;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /** Whole-model view angles (degrees) — body yaw and pitch, applied to the extracted render state. */
    public PlayerAvatarRenderer rotation(float bodyYRot, float xRot) {
        this.bodyYRot = bodyYRot;
        this.xRot = xRot;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Skin resolution, equipment, and pose are all applied fresh each render call.
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (skinLookup == null || !(context instanceof MinecraftRenderContext mcContext)) {
            return;
        }

        ClientMannequin mannequin = ensurePuppet();
        if (mannequin == null) {
            return;
        }

        mannequin.setItemSlot(EquipmentSlot.MAINHAND, heldItem);
        mannequin.setPose(Pose.STANDING);

        EntityRenderer<? super ClientMannequin, ?> renderer =
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(mannequin);
        EntityRenderState renderState = renderer.createRenderState(mannequin, 1.0F);
        if (!(renderState instanceof AvatarRenderState avatarRenderState)) {
            return;
        }

        avatarRenderState.shadowPieces.clear();
        avatarRenderState.outlineColor = 0;
        avatarRenderState.skin = skinLookup.get().playerSkin();
        avatarRenderState.bodyRot = bodyYRot;
        avatarRenderState.yRot = 0.0f;
        avatarRenderState.xRot = xRot;
        // Normalize scale to 1 the same way InventoryScreen does — the entity itself never moves
        // in a level, so its own scale/bounding-box fields are irrelevant to how big it draws here.
        avatarRenderState.boundingBoxWidth = avatarRenderState.boundingBoxWidth / avatarRenderState.scale;
        avatarRenderState.boundingBoxHeight = avatarRenderState.boundingBoxHeight / avatarRenderState.scale;
        avatarRenderState.scale = 1.0f;

        Vector2f globalPos = getGlobalPosition();
        float globalScale = getGlobalScale();
        int x0 = Math.round(globalPos.x);
        int y0 = Math.round(globalPos.y);
        int x1 = Math.round(globalPos.x + size.x * globalScale);
        int y1 = Math.round(globalPos.y + size.y * globalScale);

        float pixelHeight = size.y * globalScale;
        float scale = FIT_SCALE * pixelHeight / MODEL_HEIGHT;
        Vector3f translation = new Vector3f(0.0f, avatarRenderState.boundingBoxHeight / 2.0f, 0.0f);
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);

        mcContext.getGraphics().entity(avatarRenderState, scale, translation, rotation, null, x0, y0, x1, y1);
    }

    private ClientMannequin ensurePuppet() {
        if (puppet != null) {
            return puppet;
        }
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return null;
        }
        puppet = new ClientMannequin(level, Minecraft.getInstance().playerSkinRenderCache());
        return puppet;
    }

    @Override
    protected String getDefaultDebugName() {
        return "PlayerAvatarRenderer";
    }

    @Override
    protected PlayerAvatarRenderer self() {
        return this;
    }
}
