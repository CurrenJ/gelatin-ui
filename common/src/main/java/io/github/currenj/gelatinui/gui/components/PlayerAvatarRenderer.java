package io.github.currenj.gelatinui.gui.components;

import com.mojang.authlib.GameProfile;
import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.function.Supplier;

/**
 * A UI component that renders a fully-equipped player (skin, armor, held item) in a GUI, using
 * vanilla's own equipped-entity render pipeline rather than reimplementing item/armor layering.
 * <p>
 * Built on the same mechanism vanilla uses to preview the local player in the inventory screen
 * ({@code InventoryScreen#renderEntityInInventory}): a real player entity goes through
 * {@code EntityRenderDispatcher#render} and so through vanilla's full {@code PlayerRenderer}
 * layer stack, so armor and held items render for free. Unlike {@link PlayerModelRenderer} (a
 * bare skin), this is the component to use when equipment needs to show.
 * <p>
 * The entity behind this is a {@link RemotePlayer} puppet constructed purely as an
 * off-screen data holder: it is never added to a level and never ticked, so it never participates
 * in AI, physics, or collision. Its skin comes from the same lookup {@link PlayerModelRenderer}
 * uses, and its rotations are set on the entity right before each draw, the same way vanilla's
 * {@code InventoryScreen} overwrites the local player's body/head rotations for its preview.
 * <p>
 * Like {@link PlayerModelRenderer}, it renders through the GUI pose stack in local element
 * coordinates on 1.21.1.
 */
public class PlayerAvatarRenderer extends UIElement<PlayerAvatarRenderer> {
    private static final float MODEL_HEIGHT = 2.125f;
    private static final float FIT_SCALE = 0.97f;
    /** Every {@code PlayerModelPart} bit: hat, jacket, sleeves, pants and cape all show. */
    private static final byte ALL_MODEL_PARTS = (byte) 0x7F;

    private Supplier<PlayerSkin> skinLookup;
    private GameProfile gameProfile;
    private ItemStack heldItem = ItemStack.EMPTY;
    // 180 is vanilla's own "facing the camera" baseline for a GUI entity preview — see
    // InventoryScreen#renderEntityInInventoryFollowsMouse, which uses the same convention.
    private float bodyYRot = 180.0f;
    private float xRot = 0.0f;

    private Puppet puppet;

    public PlayerAvatarRenderer(float width, float height) {
        this.size.set(width, height);
    }

    /**
     * Resolve and render the skin for {@code profile}. The profile must already carry a UUID (a
     * player who may not be online still works, e.g. {@code new GameProfile(uuid, null)}) — see
     * {@link PlayerSkinLookup} for why name-only resolution isn't supported on this version.
     */
    public PlayerAvatarRenderer profile(GameProfile profile) {
        this.skinLookup = PlayerSkinLookup.create(profile);
        this.gameProfile = profile;
        this.puppet = null;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /** Item to show in the player's main hand. {@link ItemStack#EMPTY} for empty-handed. */
    public PlayerAvatarRenderer heldItem(ItemStack heldItem) {
        this.heldItem = heldItem != null ? heldItem : ItemStack.EMPTY;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /** Whole-model view angles (degrees) — body yaw and pitch, applied to the puppet before each draw. */
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

        Puppet mannequin = ensurePuppet();
        if (mannequin == null) {
            return;
        }

        mannequin.setItemSlot(EquipmentSlot.MAINHAND, heldItem);
        mannequin.setPose(Pose.STANDING);
        mannequin.yBodyRot = bodyYRot;
        mannequin.yBodyRotO = bodyYRot;
        mannequin.setYRot(bodyYRot);
        mannequin.yRotO = bodyYRot;
        mannequin.yHeadRot = bodyYRot;
        mannequin.yHeadRotO = bodyYRot;
        mannequin.setXRot(xRot);
        mannequin.xRotO = xRot;

        // Centered in the element box, like InventoryScreen's preview centers in its rectangle.
        // 1.20.1's renderEntityInInventory takes no separate offset param (that's a later
        // addition) and int x/y/scale instead of float, so the vertical centering that offset
        // provided is applied as an outer pose translate instead. Pixel placement not yet
        // verified in-game.
        float scale = FIT_SCALE * size.y / MODEL_HEIGHT;
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
        GuiGraphics graphics = mcContext.getGraphics();
        graphics.pose().pushPose();
        graphics.pose().translate(0.0f, mannequin.getBbHeight() / 2.0f, 0.0f);
        InventoryScreen.renderEntityInInventory(graphics, Math.round(size.x / 2.0f), Math.round(size.y / 2.0f), Math.round(scale),
                rotation, null, mannequin);
        graphics.pose().popPose();
    }

    private Puppet ensurePuppet() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            puppet = null;
            return null;
        }
        if (puppet == null || puppet.level() != level) {
            puppet = new Puppet(level, gameProfile, skinLookup);
        }
        return puppet;
    }

    /** The off-screen player this renderer draws. It is never added to a level. */
    private static final class Puppet extends RemotePlayer {
        private final Supplier<PlayerSkin> skin;

        private Puppet(ClientLevel level, GameProfile profile, Supplier<PlayerSkin> skin) {
            super(level, profile);
            this.skin = skin;
            this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, ALL_MODEL_PARTS);
            // Far below the world, so LivingEntityRenderer#shouldShowName's 64-block distance check
            // from the camera never draws a name tag over the preview.
            this.setPos(0.0, level.getMinBuildHeight() - 4096.0, 0.0);
        }

        @Override
        public net.minecraft.resources.ResourceLocation getSkinTextureLocation() {
            return skin.get().texture();
        }

        @Override
        public String getModelName() {
            return skin.get().model() == PlayerSkin.Model.SLIM ? "slim" : "default";
        }

        @Override
        public boolean isSpectator() {
            return false;
        }
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
