package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.IUIElement;
import io.github.currenj.gelatinui.gui.UIContainer;
import net.minecraft.resources.ResourceLocation;

import java.awt.geom.Rectangle2D;

/**
 * Abstract base class for panels with background support.
 * Provides background color and sprite functionality.
 * Subclasses must implement the generic self() method for fluent API.
 */
public abstract class PanelBase<T extends PanelBase<T>> extends UIContainer<T> {
    private int backgroundColor = 0x00000000; // Transparent by default
    private boolean drawBackground = false;
    private SpriteData backgroundSprite = null;
    private boolean autoSizeToChildren = false;

    public PanelBase() {
    }

    public T backgroundColor(int color) {
        this.backgroundColor = color;
        this.drawBackground = true;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T drawBackground(boolean draw) {
        this.drawBackground = draw;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /**
     * Set a sprite as the background.
     * The sprite's render mode (STRETCH, REPEAT, or SLICE) determines how it's rendered.
     */
    public T backgroundSprite(SpriteData sprite) {
        this.backgroundSprite = sprite;
        this.drawBackground = true;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /**
     * Set whether the panel should automatically size itself to fit its children.
     * If true, the panel will adjust its size based on the combined size of its children.
     */
    public T autoSizeToChildren(boolean autoSize) {
        this.autoSizeToChildren = autoSize;
        markDirty(DirtyFlag.SIZE);
        return self();
    }

    @Override
    protected void performLayout() {
        // Panel uses default bounding box layout (no special positioning)
        // Children maintain their own positions

        // If autoSizeToChildren is enabled, calculate the panel size based on children
        if (autoSizeToChildren) {
            if (children.isEmpty()) {
                // If no children, keep current size or set to minimum
                setSize(Math.max(1, size.x), Math.max(1, size.y));
                return;
            }

            // Calculate bounding box of all children relative to panel position
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            for (IUIElement child : children) {
                if (child.isVisible()) {
                    Rectangle2D childBounds = child.getBounds();
                    minX = Math.min(minX, (float) childBounds.getMinX());
                    minY = Math.min(minY, (float) childBounds.getMinY());
                    maxX = Math.max(maxX, (float) childBounds.getMaxX());
                    maxY = Math.max(maxY, (float) childBounds.getMaxY());
                }
            }

            // Set the panel size to encompass all children
            float newWidth = maxX - minX;
            float newHeight = maxY - minY;
            setSize(Math.max(1, newWidth), Math.max(1, newHeight));
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // No special update logic for basic panel
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (drawBackground) {
            int w = (int) Math.ceil(size.x);
            int h = (int) Math.ceil(size.y);

            // Render sprite if available using unified drawSprite method
            if (backgroundSprite != null && backgroundSprite.texture() != null) {
                // Update the background sprite's actual size to match the panel's current size
                SpriteData sizedSprite = backgroundSprite.actualSize(w, h);
                context.enableBlend();
                context.drawSprite(sizedSprite, 0, 0, w, h);
                context.disableBlend();
            }
            // Fall back to solid color
            else {
                context.fill(0, 0, w, h, backgroundColor);
            }
        }
    }

    protected abstract T self();

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isDrawingBackground() {
        return drawBackground;
    }

    public SpriteData getBackgroundSprite() {
        return backgroundSprite;
    }

    public boolean isAutoSizeToChildren() {
        return autoSizeToChildren;
    }
}
