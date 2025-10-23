package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIEvent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

/**
 * A progress bar component with skill level-based visual embellishments.
 * Renders a layered sprite-based progress bar with optional decorations based on skill level.
 */
public class SpriteProgressBar extends SpriteRectangle<SpriteProgressBar> {
    // Texture resources
    public static final ResourceLocation BAR_BACKGROUND = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_background.png");
    public static final ResourceLocation BAR_GOLD_OUTLINE = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_gold_outline.png");
    public static final ResourceLocation BAR_FILLED_METER = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_filled_meter.png");
    public static final ResourceLocation BAR_EMBELLISHMENT_1 = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_embellishment_1.png");
    public static final ResourceLocation BAR_EMBELLISHMENT_2 = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_embellishment_2.png");
    public static final ResourceLocation BAR_EMBELLISHMENT_3 = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_embellishment_3.png");

    // Default dimensions
    public static final int DEFAULT_WIDTH = 63;
    public static final int DEFAULT_HEIGHT = 19;

    // Sprite parameters - UV coordinates and dimensions in texture
    private static final int SPRITE_U = 10;
    private static final int SPRITE_V = 5;
    private static final int SPRITE_WIDTH = 63;
    private static final int SPRITE_HEIGHT = 19;
    private static final int TEXTURE_SIZE = 128;

    // State
    private float targetProgress = 0f;
    private float displayedProgress = 0f;
    private float animationSpeed = 5.0f; // higher = faster animation
    private boolean isAnimating = false;
    private int skillLevel = 0;

    public SpriteProgressBar() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public SpriteProgressBar(float width, float height) {
        super(width, height, 0xFFFFFFFF);
    }

    public SpriteProgressBar(Vector2f size) {
        super(size, 0xFFFFFFFF);
    }

    /**
     * Set the progress value (0.0 to 1.0).
     */
    public SpriteProgressBar progress(float progress) {
        this.targetProgress = Math.max(0f, Math.min(1f, progress));
        this.isAnimating = true;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    /**
     * Set the skill level (affects visual embellishments).
     */
    public SpriteProgressBar skillLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("Skill level cannot be negative");
        }
        this.skillLevel = level;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    /**
     * Get the current progress value.
     */
    public float getProgress() {
        return displayedProgress;
    }

    /**
     * Get the current skill level.
     */
    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        int x = 0;
        int y = 0;
        int w = (int) Math.ceil(size.x);
        int h = (int) Math.ceil(size.y);

        context.enableBlend();

        // Render background
        blitProgressBarSprite(context, BAR_BACKGROUND, x, y, w, h, 1f);

        // Render skill level decorations
        if (skillLevel >= 15) {
            blitProgressBarSprite(context, BAR_GOLD_OUTLINE, x, y, w, h, 1f);
        }
        if (skillLevel >= 30) {
            blitProgressBarSprite(context, BAR_EMBELLISHMENT_1, x, y, w, h, 1f);
        }
        if (skillLevel >= 45) {
            blitProgressBarSprite(context, BAR_EMBELLISHMENT_2, x, y, w, h, 1f);
        }
        if (skillLevel >= 60) {
            blitProgressBarSprite(context, BAR_EMBELLISHMENT_3, x, y, w, h, 1f);
        }

        // Render filled meter (progress-dependent)
        blitProgressBarSprite(context, BAR_FILLED_METER, x, y, w, h, displayedProgress);

        context.disableBlend();
    }

    /**
     * Helper method to draw a progress bar sprite with optional partial width based on progress.
     * @param context Render context
     * @param sprite Texture location
     * @param x Destination X
     * @param y Destination Y
     * @param width Destination width (component bounds)
     * @param height Destination height (component bounds)
     * @param progressAmount Progress multiplier (0.0 to 1.0) for partial rendering
     */
    private void blitProgressBarSprite(IRenderContext context, ResourceLocation sprite, int x, int y, int width, int height, float progressAmount) {
        blitProgressBarSprite(context, sprite, x, y, width, height, progressAmount, SPRITE_U, SPRITE_V, SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    /**
     * Helper method to draw a progress bar sprite with optional partial width based on progress.
     * @param context Render context
     * @param sprite Texture location
     * @param x Destination X
     * @param y Destination Y
     * @param width Destination width (component bounds)
     * @param height Destination height (component bounds)
     * @param progressAmount Progress multiplier (0.0 to 1.0) for partial rendering
     * @param srcU Source U coordinate in texture
     * @param srcV Source V coordinate in texture
     * @param srcWidth Source width in texture pixels
     * @param srcHeight Source height in texture pixels
     */
    private void blitProgressBarSprite(IRenderContext context, ResourceLocation sprite, int x, int y, int width, int height, float progressAmount, int srcU, int srcV, int srcWidth, int srcHeight) {
        if (progressAmount <= 0) {
            return;
        }

        int calculatedSrcWidth = (int) Math.ceil(srcWidth * progressAmount);
        calculatedSrcWidth = Math.max(1, Math.min(srcWidth, calculatedSrcWidth));
        int dstWidth = (int) Math.ceil(width * progressAmount);
        dstWidth = Math.max(1, Math.min(width, dstWidth));

        // Draw only the portion of the sprite corresponding to the progress amount
        context.drawTexture(sprite, x, y, dstWidth, height, srcU, srcV, calculatedSrcWidth, srcHeight, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Update displayed progress for animation
        if (isAnimating) {
            if (Math.abs(targetProgress - displayedProgress) < 0.01f) {
                displayedProgress = targetProgress;
                isAnimating = false;
            } else {
                displayedProgress += (targetProgress - displayedProgress) * animationSpeed * deltaTime;
            }
        }
    }

    @Override
    protected boolean onEvent(UIEvent event) {
        // Progress bars don't respond to events by default
        return false;
    }

    @Override
    protected String getDefaultDebugName() {
        return "SpriteProgressBar(progress=" + String.format("%.1f", displayedProgress) + ", skill=" + skillLevel + ")";
    }

    @Override
    protected SpriteProgressBar self() {
        return this;
    }
}
