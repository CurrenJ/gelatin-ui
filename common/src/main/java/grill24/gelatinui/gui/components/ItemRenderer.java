package grill24.gelatinui.gui.components;

import grill24.gelatinui.gui.DirtyFlag;
import grill24.gelatinui.gui.IRenderContext;
import grill24.gelatinui.gui.UIElement;
import grill24.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2f;

/**
 * A UI component for rendering Minecraft items.
 * Displays an ItemStack with optional count overlay.
 */
public class ItemRenderer extends UIElement {
    private ItemStack itemStack = ItemStack.EMPTY;
    private boolean showCount = true;
    private float itemScale = 1.0f;

    /**
     * Default constructor creates a 16x16 item renderer (standard item size).
     */
    public ItemRenderer() {
        this.size.set(16, 16);
    }

    /**
     * Constructor with custom size.
     */
    public ItemRenderer(float width, float height) {
        this.size.set(width, height);
    }

    /**
     * Constructor with ItemStack.
     */
    public ItemRenderer(ItemStack itemStack) {
        this.size.set(16, 16);
        this.itemStack = itemStack;
    }

    /**
     * Constructor with size and ItemStack.
     */
    public ItemRenderer(float width, float height, ItemStack itemStack) {
        this.size.set(width, height);
        this.itemStack = itemStack;
    }

    /**
     * Set the ItemStack to render.
     */
    public ItemRenderer itemStack(ItemStack itemStack) {
        if (!ItemStack.matches(this.itemStack, itemStack)) {
            this.itemStack = itemStack;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set whether to show the item count overlay.
     */
    public ItemRenderer showCount(boolean showCount) {
        if (this.showCount != showCount) {
            this.showCount = showCount;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set the scale of the rendered item.
     */
    public ItemRenderer itemScale(float scale) {
        if (this.itemScale != scale) {
            this.itemScale = scale;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Get the current ItemStack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Check if count display is enabled.
     */
    public boolean isShowCount() {
        return showCount;
    }

    /**
     * Get the item scale.
     */
    public float getItemScale() {
        return itemScale;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Items are typically static unless animated in the future
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (itemStack.isEmpty()) {
            return;
        }

        // We need access to Minecraft's rendering system for items
        // Check if we have a MinecraftRenderContext
        if (!(context instanceof MinecraftRenderContext mcContext)) {
            return;
        }

        // Render at local coordinates (0, 0) since the framework already translates by position
        int x = 0;
        int y = 0;

        // Center the item in the available space if size differs from 16x16
        if (size.x != 16 || size.y != 16) {
            x += (int) ((size.x - 16 * itemScale) / 2);
            y += (int) ((size.y - 16 * itemScale) / 2);
        }

        // Use GuiGraphics to render the item
        var graphics = mcContext.getGraphics();
        var font = mcContext.getFont();

        // Apply scaling if needed
        if (itemScale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(itemScale, itemScale, 1.0f);

            // Render at origin after scaling
            graphics.renderItem(itemStack, 0, 0);

            if (showCount && itemStack.getCount() > 1) {
                graphics.renderItemDecorations(font, itemStack, 0, 0);
            }

            graphics.pose().popPose();
        } else {
            // No scaling, render directly
            graphics.renderItem(itemStack, x, y);

            if (showCount && itemStack.getCount() > 1) {
                graphics.renderItemDecorations(font, itemStack, x, y);
            }
        }
    }

    /**
     * Get the preferred size for this item renderer.
     * This is useful for layout calculations.
     */
    public Vector2f getPreferredSize() {
        // Preferred size is the standard item size scaled
        return new Vector2f(16 * itemScale, 16 * itemScale);
    }

    @Override
    protected String getDefaultDebugName() {
        String itemName = itemStack.isEmpty() ? "empty" : itemStack.getItem().toString();
        return "ItemRenderer(item=" + itemName + ")";
    }
}
