package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIEvent;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * A clickable button based on ItemRenderer with hover and pressed visual states.
 * Provides a convenience onClick callback.
 */
public class ItemButton extends ItemRenderer<ItemButton> {
    // pressed visual timer in seconds
    private float pressedTimer = 0f;
    private static final float PRESSED_DISPLAY_TIME = 0.12f;

    protected boolean pressed = false;

    public ItemButton() {
        super();
        init();
    }

    public ItemButton(float width, float height) {
        super(width, height);
        init();
    }

    public ItemButton(ItemStack itemStack) {
        super(itemStack);
        init();
    }

    public ItemButton(float width, float height, ItemStack itemStack) {
        super(width, height, itemStack);
        init();
    }

    private void init()
    {
        // Handle hover state
        this.onMouseEnter(e -> {
            this.setTargetScale(1.5f, true);
            markDirty(io.github.currenj.gelatinui.gui.DirtyFlag.CONTENT);
        });
        this.onMouseExit(e -> {
            this.setTargetScale(1.0f, true);
            markDirty(io.github.currenj.gelatinui.gui.DirtyFlag.CONTENT);
        });
    }

    // Provide fluent overrides so chaining from ItemRenderer returns ItemButton
    @Override
    public ItemButton itemStack(ItemStack itemStack) {
        super.itemStack(itemStack);
        return this;
    }

    @Override
    public ItemButton showCount(boolean showCount) {
        super.showCount(showCount);
        return this;
    }

    @Override
    public ItemButton itemScale(float scale) {
        super.itemScale(scale);
        return this;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (pressedTimer > 0f) {
            pressedTimer -= deltaTime;
            if (pressedTimer <= 0f) {
                setPressed(false);
                pressedTimer = 0f;
            }
        }
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (getItemStack().isEmpty()) {
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
            x += (int) ((size.x - 16 * getItemScale()) / 2);
            y += (int) ((size.y - 16 * getItemScale()) / 2);
        }

        // Use GuiGraphics to render the item
        var graphics = mcContext.getGraphics();
        var font = mcContext.getFont();

        // Calculate effective scale including hover/pressed effects
        float effectiveScale = getItemScale();

        // Apply scaling
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(effectiveScale, effectiveScale, 1.0f);

        // Render at origin after scaling
        graphics.renderItem(getItemStack(), 0, 0);

        if (isShowCount() && getItemStack().getCount() > 1) {
            graphics.renderItemDecorations(font, getItemStack(), 0, 0);
        }

        graphics.pose().popPose();
    }

    /**
     * Protected helper for subclasses to set pressed state.
     */
    protected void setPressed(boolean pressed) {
        if (this.pressed != pressed) {
            this.pressed = pressed;
            markDirty(io.github.currenj.gelatinui.gui.DirtyFlag.CONTENT);
        }
    }

    @Override
    protected String getDefaultDebugName() {
        String itemName = getItemStack().isEmpty() ? "empty" : getItemStack().getItem().toString();
        return "ItemButton(item=" + itemName + ")";
    }

    @Override
    protected ItemButton self() {
        return this;
    }

    @Override
    protected boolean onEvent(UIEvent event) {
        boolean result = super.onEvent(event);

        // Handle mouse click events
        if (event.getType() == UIEvent.Type.CLICK) {
            playClickBounce();
            return true;
        }

        return result;
    }
}
